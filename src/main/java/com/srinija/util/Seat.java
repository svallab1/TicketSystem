package com.srinija.util;

import java.util.HashMap;
import java.util.Map;
import static com.srinija.util.VenueLevel.*;

public class Seat {

	private String id;
	public SeatStatus status = SeatStatus.AVAILABLE;
	private VenueLevel level;

	private final static Map<VenueLevel, Integer> priceMap = new HashMap<VenueLevel, Integer>();
	static {
		priceMap.put(ORCHESTRA, 100);
		priceMap.put(MAIN, 75);
		priceMap.put(BALCONY1, 50);
		priceMap.put(BALCONY2, 40);
	}

	public static int getPrice(VenueLevel level) {
		return priceMap.get(level);
	}

	public Seat(String id, VenueLevel level) {
		this.id = id;
		this.level = level;

	}

	public SeatStatus getStatus() {
		return status;
	}

	public void setStatus(SeatStatus status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public VenueLevel getLevel() {
		return level;
	}

	public void setLevel(VenueLevel level) {
		this.level = level;
	}
}
