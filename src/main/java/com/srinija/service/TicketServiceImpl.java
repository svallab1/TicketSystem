package com.srinija.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.srinija.util.Seat;
import com.srinija.util.SeatHold;
import com.srinija.util.SeatLevel;
import com.srinija.util.SeatingGroup;

public class TicketServiceImpl implements TicketService {

	private final int orchestraRowCount = 25;
	private final int orchestraSeatsPerRow = 50;
	private final int orchestraTotalCount = 1250;
	private final int orchTktPrice = 100;

	private final int mainRowCount = 20;
	private final int mainSeatsPerRow = 100;
	private final int mainTotalCount = 2000;
	private final int mainTktPrice = 75;

	private final int balc1RowCount = 15;
	private final int balc1SeatsPerRow = 100;
	private final int balc1TotalCount = 1500;
	private final int balc1TktPrice = 50;

	private final int balc2RowCount = 15;
	private final int balc2SeatsPerRow = 100;
	private final int balc2TotalCount = 1500;
	private final int balc2TktPrice = 40;

	private List<SeatingGroup> seats;

	public TicketServiceImpl() {
		seats = new ArrayList<SeatingGroup>(4);
		seats.add(new SeatingGroup(SeatLevel.ORCHESTRA));
		seats.add(new SeatingGroup(SeatLevel.MAIN));
		seats.add(new SeatingGroup(SeatLevel.BALCONY1));
		seats.add(new SeatingGroup(SeatLevel.BALCONY2));

	}

	private int orchestraAvailableSeats = orchestraTotalCount;
	private int orchestraHoldSeatCount = 0;

	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {

		int level = 0, count = 0;
		if (venueLevel.isPresent()) {
			level = venueLevel.get();
		}

		synchronized (this) {
			count = seats.get(level).getAvailableSeats();
		}
		return count;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		// TODO Auto-generated method stub
		return null;
	}

}
