package com.srinija.service;

import java.util.Optional;

import static org.junit.Assert.*;
import org.junit.Test;

public class TicketServiceTest {

	private TicketServiceImpl obj = new TicketServiceImpl();
	@Test
	public void testGetCount(){
		
		int count = obj.numSeatsAvailable(Optional.of(0));
		assertEquals(1250, count);
	}
}
