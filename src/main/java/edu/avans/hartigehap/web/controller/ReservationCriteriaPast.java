package edu.avans.hartigehap.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import edu.avans.hartigehap.domain.Reservation;

public class ReservationCriteriaPast implements ReservationCriteria {

	@Override
	public List<Reservation> meetCriteria(List<Reservation> reservations) {
		List<Reservation> pastReservations = new ArrayList<>();
		DateTime now = new DateTime();
		for(Reservation reservation : reservations){
			//Reservation is in the past
			if(reservation.getEndDate().isBefore(now)){
				pastReservations.add(reservation);
			}
		}	
		return pastReservations;
	}
}
