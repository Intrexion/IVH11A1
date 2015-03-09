package edu.avans.hartigehap.model;

import edu.avans.hartigehap.domain.Restaurant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationModel {
	private Restaurant restaurant;
	private String date;
	private String startTime;
	private String endTime;
	
	private String firstName;
	private String lastName;
	private int partySize;
	private String email;
	private String phone;
}
