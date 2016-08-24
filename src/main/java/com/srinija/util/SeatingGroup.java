package com.srinija.util;

import static com.srinija.util.SeatStatus.AVAILABLE;
import static com.srinija.util.SeatStatus.BOOKED;
import static com.srinija.util.SeatStatus.HOLD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class SeatingGroup {

	private VenueLevel level;

	private int rowCount = 0;
	private int seatsPerRow = 0;

	private Map<String, SeatStatus> seatsMap;
	private SortedSet<String> availableSeats;

	public SeatingGroup(VenueLevel level) {
		this.level = level;
		if (level == VenueLevel.ORCHESTRA) {
			rowCount = 25;
			seatsPerRow = 50;
		} else if (level == VenueLevel.MAIN) {
			rowCount = 20;
			seatsPerRow = 100;
		} else if (level == VenueLevel.BALCONY1) {
			rowCount = 15;
			seatsPerRow = 100;
		} else {
			rowCount = 15;
			seatsPerRow = 100;
		}

		seatsMap = new HashMap<String, SeatStatus>();

		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < seatsPerRow; j++) {
				seatsMap.put("" + (char) ('A' + i) + (j + 1), AVAILABLE);
			}
		}
		availableSeats = new TreeSet<String>(seatsMap.keySet());

	}

	public synchronized List<Seat> holdSeats(int count) {
		if (count <= 0 || count > availableSeats.size())
			return null;

		List<Seat> heldSeats = new ArrayList<Seat>();
		for (int i = 0; i < count; i++) {
			String seatId = availableSeats.first();
			availableSeats.remove(seatId);
			SeatStatus currentStatus = seatsMap.get(seatId);
			if (currentStatus != null && currentStatus.equals(AVAILABLE))
				seatsMap.put(seatId, HOLD);

			heldSeats.add(new Seat(seatId, this.level));
		}

		return heldSeats;
	}

	public synchronized void releaseSeats(List<Seat> heldSeats) {
		if (heldSeats == null)
			return;
		for (Seat s : heldSeats) {
			if (s.getLevel() == this.level) {
				String seatId = s.getId();
				if (seatsMap.get(seatId) != BOOKED) {
					availableSeats.add(seatId);
					seatsMap.put(seatId, AVAILABLE);
				}
			}
		}
	}

	public synchronized boolean confirmSeats(List<Seat> seatIds) {
		if (seatIds == null)
			return false;
		for (Seat s : seatIds) {
			if (s.getLevel() == this.level) {
				String seatId = s.getId();
				if (seatsMap.get(seatId) == HOLD) {
					seatsMap.put(seatId, BOOKED);
				} else
					return false;
			}
		}
		return true;
	}

	public VenueLevel getLevel() {
		return level;
	}

	public void setLevel(VenueLevel level) {
		this.level = level;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getSeatsPerRow() {
		return seatsPerRow;
	}

	public void setSeatsPerRow(int seatsPerRow) {
		this.seatsPerRow = seatsPerRow;
	}
	
	public synchronized int getAvailableSeats(){
		return availableSeats.size();
	}
}
