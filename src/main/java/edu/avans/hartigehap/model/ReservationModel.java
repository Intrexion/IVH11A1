package edu.avans.hartigehap.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationModel {
	private String restaurantId;
	private String date;
	private String startTime;
	private String endTime;
	
	private String firstName;
	private String lastName;
	private int partySize;
	private String email;
	private String phone;
}
