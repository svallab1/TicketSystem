package com.srinija.util;

public enum VenueLevel {
	ORCHESTRA(0), MAIN(1), BALCONY1(2), BALCONY2(3);
	private int level;
	
	VenueLevel(int level){
		this.level = level;
	}
	
	public int getLevelAsInt(){
		return this.level;
	}
}