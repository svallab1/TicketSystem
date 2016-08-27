package com.srinija.util;

import static com.srinija.util.AppConstants.priceMap;

public class Seat {

	private Integer id;
	private VenueLevel level;

	public static int getPrice(VenueLevel level) {
		return priceMap.get(level);
	}

	public Seat(Integer id, VenueLevel level) {
		this.id = id;
		this.level = level;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public VenueLevel getLevel() {
		return level;
	}

	public void setLevel(VenueLevel level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return new String("{Seat id: " + id + "; Seat Level: " + level+"}");

	}

	@Override
	public boolean equals(Object obj) {
		Seat s = (Seat) obj;
		if (this.id == s.getId() && this.getLevel() == s.getLevel())
			return true;
		return false;
	}

}
