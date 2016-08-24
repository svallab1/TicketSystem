package com.srinija.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.srinija.util.Seat;
import com.srinija.util.SeatHold;
import com.srinija.util.VenueLevel;
import com.srinija.util.SeatingGroup;

public class TicketServiceImpl implements TicketService {

	public static List<SeatingGroup> seats;

	private Map<Integer, SeatHold> seatHolds;
	private static AtomicInteger seqNum = new AtomicInteger(1);
	private MyTimerTask t;

	private Logger logger = Logger.getLogger(TicketServiceImpl.class.getName());
	
	public TicketServiceImpl() {
		seats = new ArrayList<SeatingGroup>(4);
		seats.add(new SeatingGroup(VenueLevel.ORCHESTRA));
		seats.add(new SeatingGroup(VenueLevel.MAIN));
		seats.add(new SeatingGroup(VenueLevel.BALCONY1));
		seats.add(new SeatingGroup(VenueLevel.BALCONY2));
		
		seatHolds = new HashMap<>();

		t = new MyTimerTask();
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(t, 0, 5);
	}

	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {

		int level = 0, count = 0;
		if (venueLevel.isPresent()) {
			level = venueLevel.get();
		}
		logger.info("Request to numSeatsAvailable - venueLevel: "+level);

		count = seats.get(level).getAvailableSeats();

		return count;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail) {
		logger.info("Request to findAndHoldSeats - numSeats: "+numSeats);

		if (numSeats <= 0 || customerEmail == null)
			return null;

		int aMinLevel = 0, aMaxLevel = 3;
		if (minLevel.isPresent()) {
			aMinLevel = minLevel.get();
		}
		if (maxLevel.isPresent()) {
			aMaxLevel = maxLevel.get();
		}

		if (aMaxLevel < aMinLevel)
			return null;

		if (numSeats <= seats.get(aMinLevel).getAvailableSeats()) {
			List<Seat> heldSeats = seats.get(aMinLevel).holdSeats(numSeats);
			return new SeatHold(seqNum.getAndIncrement(), heldSeats);
		} else {
			int possibleSeats = seats.get(aMinLevel).getAvailableSeats();

			List<Seat> heldSeats = seats.get(aMinLevel).holdSeats(possibleSeats);
			SeatHold s = findAndHoldSeats(numSeats - possibleSeats, Optional.of(aMinLevel + 1), maxLevel,
					customerEmail);

			if (s == null) {
				/* Assumed that we don't allocate partial seats */

				seats.get(aMinLevel).releaseSeats(heldSeats);
				return null;
			}
			SeatHold seatHold = new SeatHold(seqNum.getAndIncrement());
			seatHold.setSeats(heldSeats);
			seatHold.getSeats().addAll(s.getSeats());
			seatHolds.put(seatHold.getId(), seatHold);
			return seatHold;
		}
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		
		logger.info("Request to reserveSeats - seatHoldId: "+seatHoldId);

		if(seatHoldId == 0 || seatHoldId >= seqNum.get() || "".equals(customerEmail))
			//Invalid SeatHold obj or user.
			return null;

		SeatHold seatHold = seatHolds.get(seatHoldId);
		for(SeatingGroup sg :  seats){
			if(sg.confirmSeats(seatHold.getSeats())) return "SUCCESS";
			else sg.releaseSeats(seatHold.getSeats());
		}
		return "FAILED";
	}
	

	@Override
	protected void finalize() throws Throwable {
		t.cancel();
		super.finalize();
	}
	
	
	//Thread to check for hold expiration.
	class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			// Check for expired SeatHolds and release them
			for(Integer s: seatHolds.keySet()){
				if(seatHolds.get(s).isExpired()) seatHolds.get(s).inValidate();
			}
		}
		
	}
}


