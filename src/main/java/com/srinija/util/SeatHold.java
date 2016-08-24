package com.srinija.util;

import java.util.List;

import com.srinija.service.TicketServiceImpl;

public class SeatHold {

	private int id;
	private List<Seat> seats;
	private int totalPrice;
	private long expireAt ;

	public SeatHold(int id) {
		this.id = id;
	}

	public SeatHold(int id, List<Seat> seats) {
		this.id = id;
		setSeats(seats);
		
	}

	public List<Seat> getSeats() {
		return seats;
	}

	public void setSeats(List<Seat> seats) {
		this.seats = seats;

		int temp = 0;
		for (int i = 0; seats != null && i < seats.size(); i++) {
			temp += Seat.getPrice(seats.get(i).getLevel());
		}
		totalPrice = temp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setExpireAt(){
		this.expireAt = System.currentTimeMillis();
	}
	
	public boolean isExpired(){
		return (expireAt -System.currentTimeMillis()) > 0; 
	}
	
	public void inValidate(){
		for(SeatingGroup sg : TicketServiceImpl.seats){
			sg.releaseSeats(getSeats());
		}
	}

}
