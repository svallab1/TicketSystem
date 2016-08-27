package com.srinija.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.srinija.util.AppConstants;
import com.srinija.util.Seat;
import com.srinija.util.SeatHold;
import com.srinija.util.SeatingGroup;
import com.srinija.util.VenueLevel;

public class TicketServiceImpl implements TicketService {

	public static List<SeatingGroup> seats;

	// Not using static - Assuming there will be only once instance of the
	// service.
	private Map<Integer, SeatHold> seatHolds;
	private static AtomicInteger seqNum = new AtomicInteger(1);
	private MyTimerTask t;

	private static Logger logger = Logger.getLogger(TicketServiceImpl.class.getName());

	public static void main(String[] args) {
		System.out.println(
				"Welcome to Ticket systems.\nThis systems finds and holds the best set of requested #  tickets.\nTickets are on hold for only 10 sec.\nPlease try to complete your transaction before it expires."
						.toUpperCase());

		Scanner sc = new Scanner(System.in);
		TicketServiceImpl obj = new TicketServiceImpl();
		while (true) {
			try {

				System.out.println(
						"Please select an operation.\n1.Check available tickets.\n2. Hold tickets\n3. Confirm tickets\n4. Exit");
				int option = sc.nextInt();
				if (option == 1) {
					System.out.println("Please enter venue level you would like to check.\n 0 : ORCHESTRA\n1 : MAIN\n"
							+ "2 : BALCONY1\n3 : BALCONY2\n4 : ALL");
					int input = sc.nextInt();
					if (input == 4) {
						for (int i = 0; i < 4; i++) {
							System.out.println(
									"LEVEL " + i + " - Available seats: " + obj.numSeatsAvailable(Optional.of(i)));
						}
					} else if (input >= 0 && input < 4) {
						System.out.println(
								"LEVEL " + input + " - Available seats: " + obj.numSeatsAvailable(Optional.of(input)));
					}
				} else if (option == 2) {
					String input = System.console().readLine("Please enter numSeats emailAddress minLevel maxLevel: ");
					String[] s = input.split(" ");
					SeatHold sh;
					if (s.length == 2)
						sh = obj.findAndHoldSeats(Integer.valueOf(s[0]), Optional.empty(), Optional.empty(), s[1]);
					else if (s.length == 3)
						sh = obj.findAndHoldSeats(Integer.valueOf(s[0]), Optional.of(Integer.valueOf(s[2])),
								Optional.empty(), s[1]);
					else if (s.length == 4)
						sh = obj.findAndHoldSeats(Integer.valueOf(s[0]), Optional.of(Integer.valueOf(s[2])),
								Optional.of(Integer.valueOf(s[3])), s[1]);
					else
						throw new IOException(
								"Input incorrect format.\nnumSeat - int, emailAddress - String, Optional[ minLevel - int, maxLevel - int]");
					if (sh != null) {
						System.out.println(sh);
					}
				} else if (option == 3) {
					String input = System.console()
							.readLine("To confirm your tickets, please enter your SeatHold id and email address: ");
					String[] s = input.split(" ");
					if (s.length != 2)
						throw new IOException("Input incorrect format.\nSeat Hold if - int, emailAddress - String");
					System.out.println(obj.reserveSeats(Integer.valueOf(s[0]), s[1]));
				} else {
					System.out.println("Good bye.\n");
					break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please try again");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		sc.close();

	}

	public TicketServiceImpl() {
		seats = new ArrayList<SeatingGroup>(4);
		seats.add(new SeatingGroup(VenueLevel.ORCHESTRA));
		seats.add(new SeatingGroup(VenueLevel.MAIN));
		seats.add(new SeatingGroup(VenueLevel.BALCONY1));
		seats.add(new SeatingGroup(VenueLevel.BALCONY2));

		seatHolds = new HashMap<>();

		t = new MyTimerTask();
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(t, 0, 500);
	}

	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {

		int level = 0, count = 0;
		if (venueLevel.isPresent()) {
			level = venueLevel.get();
		}
		logger.fine("Request numSeatsAvailable - venueLevel: " + level);

		if (level >= 0 && level < 4) {
			count = seats.get(level).getAvailableSeats();
		} else {
			logger.info("Invalid level selected.");
		}
		return count;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail) {

		if (numSeats <= 0 || customerEmail == null || !isEmailValid(customerEmail)) {
			logger.info("Incorrect hold request. Please provide valid numSeats and/or email");
			return null;
		}

		int aMinLevel = 0, aMaxLevel = 3;
		if (minLevel.isPresent()) {
			aMinLevel = minLevel.get();
		}
		if (maxLevel.isPresent()) {
			aMaxLevel = maxLevel.get();
		}

		if (aMaxLevel < aMinLevel) {
			logger.info("Incorrect hold request. Please provide correct VenueLevels");
			return null;
		}
		logger.fine("Request findAndHoldSeats - numSeats: " + numSeats + "; customer Email: " + customerEmail
				+ "; min venue level:" + minLevel + "; max level: " + maxLevel);

		if (numSeats <= seats.get(aMinLevel).getAvailableSeats()) {
			List<Seat> heldSeats = seats.get(aMinLevel).holdSeats(numSeats);
			SeatHold seatHold = new SeatHold(seqNum.getAndIncrement(), heldSeats);
			seatHold.setEmail(customerEmail);
			synchronized (TicketServiceImpl.class) {
				seatHolds.put(seatHold.getId(), seatHold);
			}
			logger.info("SeatHold ID: " + seatHold.getId() + " is returned to user.");
			return seatHold;
		} else {
			int possibleSeats = seats.get(aMinLevel).getAvailableSeats();

			List<Seat> heldSeats = seats.get(aMinLevel).holdSeats(possibleSeats);

			// Recursive call to find remaining seats in the next venue level.
			SeatHold shSubset = findAndHoldSeats(numSeats - possibleSeats, Optional.of(aMinLevel + 1), maxLevel,
					customerEmail);

			if (shSubset == null) {
				/* Assumed that we don't allocate partial seats */

				logger.info("Could not allocate " + numSeats + ". Please try a different number");
				seats.get(aMinLevel).releaseSeats(heldSeats);
				return null;
			}
			SeatHold seatHold = new SeatHold(seqNum.getAndIncrement());
			seatHold.setSeats(heldSeats);
			seatHold.getSeats().addAll(shSubset.getSeats());
			synchronized (TicketServiceImpl.class) {
				seatHolds.put(seatHold.getId(), seatHold);
			}
			seatHold.setEmail(customerEmail);
			logger.info("SeatHold ID: " + seatHold.getId() + " is returned to user.");
			return seatHold;
		}
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {

		logger.fine("Request to reserveSeats - seatHoldId: " + seatHoldId + "; customerEmail: " + customerEmail);

		SeatHold seatHold = seatHolds.get(seatHoldId);

		if (seatHoldId == 0 || seatHoldId >= seqNum.get() || seatHold == null || "".equals(customerEmail)
				|| !seatHold.getEmail().equals(customerEmail)) {
			logger.info("Invalid seat hold id or email.");
			return "INCORRECT RESERVATION";
		}

		for (SeatingGroup sg : seats) {
			if (sg.confirmSeats(seatHold.getSeats())){
				synchronized (TicketServiceImpl.class) {
					seatHolds.remove(seatHold);
				}
				return AppConstants.SUCCESS_CONFIRMATION;
			}
			else
				sg.releaseSeats(seatHold.getSeats());
		}
		return AppConstants.FAILED_CONFIRMATION;
	}

	private boolean isEmailValid(String email) {
		// Can be a complex regular expression to make a better match.
		if (email.matches("([\\w]*)@([\\w]*)\\.([\\w]*)"))
			return true;
		return false;
	}

	@Override
	protected void finalize() throws Throwable {
		// Clean up the scheduled thread from memory.
		t.cancel();
		super.finalize();
	}

	// Scheduled task to check for SeatHold expiration.
	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			// Check for expired SeatHolds and release them
			logger.info(this.getClass().getName() + " Checking for expired SeatHolds.");
			for (Integer s : seatHolds.keySet()) {
				if (seatHolds.get(s).isExpired()) {
					logger.info(s + "Expired.");
					seatHolds.get(s).inValidate();
					synchronized (TicketServiceImpl.class) {
						seatHolds.remove(s);
					}
				}
			}
		}

	}
}
