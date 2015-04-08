package edu.avans.hartigehap.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import edu.avans.hartigehap.domain.Reservation;

public class ReservationCriteriaNow implements ReservationCriteria {

	@Override
	public List<Reservation> meetCriteria(List<Reservation> reservations) {
		List<Reservation> nowReservations = new ArrayList<>();
		DateTime now = new DateTime();
		for(Reservation reservation : reservations){
			//Reservations between half hour before now and half hour after now.
			if(reservation.getStartDate().isAfter(now.minusMinutes(30)) && reservation.getStartDate().isBefore(now.plusMinutes(30))){
				nowReservations.add(reservation);
			}
		}	
		return nowReservations;
	}
}
