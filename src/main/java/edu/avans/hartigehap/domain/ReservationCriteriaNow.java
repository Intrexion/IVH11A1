package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

public class ReservationCriteriaNow implements ReservationCriteria {

	@Override
	public List<Reservation> meetCriteria(List<Reservation> reservations) {
		List<Reservation> nowReservations = new ArrayList<>();
		DateTime now = new DateTime();
		for(Reservation reservation : reservations){
			if(reservation.getStartDate().isAfter(now.minusMinutes(30)) && reservation.getStartDate().isBefore(now.plusMinutes(30))){
				nowReservations.add(reservation);
			}
		}	
		return nowReservations;
	}
}
