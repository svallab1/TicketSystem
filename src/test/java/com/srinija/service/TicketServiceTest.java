package com.srinija.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.srinija.util.AppConstants;
import com.srinija.util.Seat;
import com.srinija.util.SeatHold;

public class TicketServiceTest {

	private final String validEmail = "srinija@domain.com";
	private final String inValidEmail = "srinija@com";

	private TicketService obj = new TicketServiceImpl();

	@Test
	public void testGetCountOrchestra() {

		int count = obj.numSeatsAvailable(Optional.of(0));
		assertEquals(1250, count);
	}

	@Test
	public void testGetCountMain() {

		int count = obj.numSeatsAvailable(Optional.of(1));
		assertEquals(2000, count);
	}

	@Test
	public void testGetCountBalcony1() {

		int count = obj.numSeatsAvailable(Optional.of(2));
		assertEquals(1500, count);
	}

	@Test
	public void testGetCountBalcony2() {

		int count = obj.numSeatsAvailable(Optional.of(3));
		assertEquals(1500, count);
	}

	@Test
	public void testInvalidEmail() {
		SeatHold sh = obj.findAndHoldSeats(10, Optional.of(0), Optional.of(3), inValidEmail);
		assertEquals(null, sh);
	}

	@Test
	public void testGetSeats() {
		SeatHold sh = obj.findAndHoldSeats(55, Optional.of(0), Optional.of(0), validEmail);
		assertEquals("Incorrect seat count", 55, sh.getSeats().size());
	}

	@Test
	public void testGetSeatsOverCapacity() {
		SeatHold sh = obj.findAndHoldSeats(1251, Optional.of(0), Optional.of(0), validEmail);
		assertTrue(sh == null);
	}

	@Test
	public void testSeatHoldExpiry() {
		SeatHold sh = obj.findAndHoldSeats(55, Optional.of(0), Optional.of(0), validEmail);
		try {
			// Sleep for 12sec or expire hold manually.
			// Thread.sleep(12000);
			sh.setExpireAt(0);
			Thread.sleep(600);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(sh.isExpired());
		String resp = obj.reserveSeats(sh.getId(), validEmail);
		assertEquals("", AppConstants.INCORRECT_RESERVATION, resp);
	}

	@Test
	public void testHoldTicketsFromMultipleLevels() {
		SeatHold sh = obj.findAndHoldSeats(1250, Optional.of(0), Optional.of(1), validEmail);
		assertEquals(1250, sh.getSeats().size());
	}

	// Multi thread access
	@Test
	public void testMultiThread() {
		class R implements Runnable {
			@Override
			public void run() {
				SeatHold sh = obj.findAndHoldSeats(10, Optional.empty(), Optional.empty(), validEmail);
				assertEquals(AppConstants.SUCCESS_CONFIRMATION, obj.reserveSeats(sh.getId(), sh.getEmail()));
			}
		}
		;
		class R2 implements Runnable {
			@Override
			public void run() {
				SeatHold sh = obj.findAndHoldSeats(10, Optional.empty(), Optional.empty(), validEmail);
				sh.setExpireAt(0);
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
				assertEquals(AppConstants.INCORRECT_RESERVATION, obj.reserveSeats(sh.getId(), sh.getEmail()));
			}
		}
		;

		Thread[] threads = new Thread[5];
		for (int i = 0; i < 3; i++) 
			threads[i] = new Thread(new R());
		for (int i = 3; i < 5; i++)
			threads[i] = new Thread(new R2());

		for(int i = 0 ; i < 5 ; i++)
			threads[i].start();
		try {
			for(int i = 0 ; i < 5 ; i++)
				threads[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
