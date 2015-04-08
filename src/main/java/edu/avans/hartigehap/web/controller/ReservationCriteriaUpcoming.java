package edu.avans.hartigehap.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import edu.avans.hartigehap.domain.Reservation;

public class ReservationCriteriaUpcoming implements ReservationCriteria {

	@Override
	public List<Reservation> meetCriteria(List<Reservation> reservations) {
		List<Reservation> upcomingReservations = new ArrayList<>();
		DateTime now = new DateTime();
		for(Reservation reservation : reservations){
			//End of the reservation is after now
			if(reservation.getEndDate().isAfter(now)){
				upcomingReservations.add(reservation);
			}
		}	
		return upcomingReservations;
	}
}
