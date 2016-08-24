package com.srinija.util;

public class SeatingGroup {

	private SeatLevel level;
	
	private int rowCount= 0;
	private int seatsPerRow = 0;
	
	private int availableSeats = 0;
	private int holdSeats = 0;
	
	public SeatingGroup(SeatLevel level){
		this.level = level;
		if(level == SeatLevel.ORCHESTRA){
			rowCount = 25;
			seatsPerRow = 50;
			availableSeats = 1250;
			holdSeats = 0;
		}else if(level == SeatLevel.MAIN) {
			rowCount = 20;
			seatsPerRow = 100;
			availableSeats = 2000;
			holdSeats = 0;
		}else if(level == SeatLevel.BALCONY1) {
			rowCount = 15;
			seatsPerRow = 100;
			availableSeats = 1500;
			holdSeats = 0;
		}else {
			rowCount = 15;
			seatsPerRow = 100;
			availableSeats = 1500;
			holdSeats = 0;
		}
	}

	public SeatLevel getLevel() {
		return level;
	}

	public void setLevel(SeatLevel level) {
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

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
	}

	public int getHoldSeats() {
		return holdSeats;
	}

	public void setHoldSeats(int holdSeats) {
		this.holdSeats = holdSeats;
	}
	
	
}
