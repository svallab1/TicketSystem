package com.srinija.util;

import java.util.List;

import com.srinija.service.TicketServiceImpl;

public class SeatHold {

	private int id;
	private List<Seat> seats;
	private int totalPrice;
	private String userEmail;
	private long expireAt;

	private static final int lifeTime = 20000; // seat hold expires after 20
												// seconds.

	public SeatHold(int id, List<Seat> seats) {
		this.id = id;
		setSeats(seats);
		setExpireAt(lifeTime);
	}

	public SeatHold(int id) {
		this.id = id;
		setExpireAt(lifeTime);
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

	public int getTotalPrice() {
		return totalPrice;
	}

	public synchronized void setExpireAt(int t) {
		this.expireAt = System.currentTimeMillis() + t;
	}

	public synchronized boolean isExpired() {
		return (expireAt - System.currentTimeMillis()) < 0;
	}

	// Invalidate the SeatHold object.
	public synchronized void inValidate() {
		for (SeatingGroup sg : TicketServiceImpl.seats) {
			sg.releaseSeats(getSeats());
		}
	}

	public void setEmail(String email) {
		this.userEmail = email;
	}

	public String getEmail() {
		return this.userEmail;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Transaction id: "+this.id);
		sb.append("; Email id: "+userEmail);
		sb.append("; Seats: "+this.seats);
		return sb.toString();
	}

}
