package com.srinija.util;

import static com.srinija.util.SeatStatus.AVAILABLE;
import static com.srinija.util.SeatStatus.BOOKED;
import static com.srinija.util.SeatStatus.HOLD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatingGroup {

	private VenueLevel level;

	private int rowCount = 0;
	private int seatsPerRow = 0;

	private Map<Integer, SeatStatus> seatsMap;
	private List<Integer> seatList;

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

		seatsMap = new HashMap<Integer, SeatStatus>();

		//Initialize all seats in the group to AVAILABLE.
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < seatsPerRow; j++) {
				seatsMap.put(Integer.valueOf(
						level.getLevelAsInt() + "" + String.format("%02d", i + 1) + String.format("%03d", (j + 1))),
						AVAILABLE);
			}
		}
		// availableSeats = new TreeSet<Integer>(seatsMap.keySet());
		seatList = new ArrayList<Integer>(seatsMap.keySet());

	}

	public synchronized List<Seat> holdSeats(int count) {
		if (count <= 0 || count > seatList.size())
			return null;

		Collections.sort(seatList);
		return getBestSeats(count);
	}

	/*
	 * Assumption: Best seat criteria: Longest subsequence in minimum venue
	 * level. That is, in a given venue level, maximum possible sets of
	 * continous seats(sets with decreasing order of length. Here sets do not
	 * refer to Collections Set).
	 */
	private List<Seat> getBestSeats(int count) {

		int startPoint = 0, maxLenth = 0;
		for (int i = 0; i < seatList.size();) {

			int j = seatList.get(i);
			while (seatList.contains(j) && seatsMap.get(j).equals(AVAILABLE))
				j++;
			int temp = seatList.indexOf(j - 1) - i + 1;
			if (maxLenth < temp) {
				maxLenth = temp;
				startPoint = i;
				if (maxLenth >= count) {
					List<Integer> result = seatList.subList(startPoint, startPoint + count);
					List<Seat> heldSeats = new ArrayList<Seat>();
					for (Integer k : result) {
						seatsMap.put(k, HOLD);
						heldSeats.add(new Seat(k, level));
					}
					seatList.removeAll(result);
					return heldSeats;
				}
			}
			i += maxLenth;
		}

		List<Integer> result = seatList.subList(startPoint, startPoint + maxLenth);
		List<Seat> heldSeats = new ArrayList<Seat>();
		for (Integer i : result) {
			seatsMap.put(i, HOLD);
			heldSeats.add(new Seat(i, level));
		}
		seatList.removeAll(result);
		heldSeats.addAll(getBestSeats(count - maxLenth));
		return heldSeats;
	}

	/*
	 * Release only seats that are in HOLD state. Assumption: a booked ticket
	 * cannot be cancelled.
	 */
	public synchronized void releaseSeats(List<Seat> heldSeats) {
		if (heldSeats == null)
			return;
		for (Seat s : heldSeats) {
			if (s.getLevel() == this.level) {
				Integer seatId = s.getId();
				if (seatsMap.get(seatId) == HOLD) {
					seatList.add(seatId);
					seatsMap.put(seatId, AVAILABLE);
				}
			}
		}
	}

	/*
	 * Assumption: Only tickets on HOLD can be BOOKED.
	 */
	public synchronized boolean confirmSeats(List<Seat> seatIds) {
		if (seatIds == null)
			return false;
		for (Seat s : seatIds) {
			if (s.getLevel() == this.level) {
				Integer seatId = s.getId();
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

	public synchronized int getAvailableSeats() {
		return seatList.size();
	}

	@Override
	public String toString() {
		return seatsMap.toString();
	}
}
