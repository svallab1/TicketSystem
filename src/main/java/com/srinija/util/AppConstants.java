package com.srinija.util;

import static com.srinija.util.VenueLevel.BALCONY1;
import static com.srinija.util.VenueLevel.BALCONY2;
import static com.srinija.util.VenueLevel.MAIN;
import static com.srinija.util.VenueLevel.ORCHESTRA;

import java.util.HashMap;
import java.util.Map;

public class AppConstants {

	public static final String INCORRECT_RESERVATION = "INCORRECT RESERVATION";
	public static final String INCORRECT_EMAIL = "INCORRECT EMAIL";
	public static final String SUCCESS_CONFIRMATION = "SUCCESSFULLY CONFIRMED YOUR SEATS";
	public static final String FAILED_CONFIRMATION = "UNABLE TO CONFIRM YOUR TICKETS. PLEASE TRY AGAIN";
	public final static Map<VenueLevel, Integer> priceMap = new HashMap<VenueLevel, Integer>();
	static {
		priceMap.put(ORCHESTRA, 100);
		priceMap.put(MAIN, 75);
		priceMap.put(BALCONY1, 50);
		priceMap.put(BALCONY2, 40);
	}
}
