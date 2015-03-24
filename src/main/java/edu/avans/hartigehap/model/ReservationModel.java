package edu.avans.hartigehap.model;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationModel {
	private String restaurantId;
	private String date;
	private String startTime;
	private String endTime;
	
	@NotEmpty(message = "{validation.firstname.NotEmpty.message}")
	private String firstName;
	@NotEmpty(message = "{validation.lastname.NotEmpty.message}")
	private String lastName;
	private int partySize;
	private String email;
	private String phone;
	
	private String description;
}
