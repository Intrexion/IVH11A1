package edu.avans.hartigehap.web.controller;

import org.joda.time.DateTime;

public class DateTimeConverter {

	public static DateTime stringToDateTime(String date, String time){
		
		date = date.substring(0,10);
		String[] splitDate = date.split("-");  
		String[] splitTime = time.split(":");
		int year = Integer.parseInt(splitDate[0]);
		int month = Integer.parseInt(splitDate[1]);
		int day = Integer.parseInt(splitDate[2]);
		int hour = Integer.parseInt(splitTime[0]);
		int minute = Integer.parseInt(splitTime[1]);
		
		return new DateTime(year, month, day, hour, minute);
	}
	
}
