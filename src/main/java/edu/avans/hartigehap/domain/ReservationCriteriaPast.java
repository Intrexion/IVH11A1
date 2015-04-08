package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

public class ReservationCriteriaPast implements ReservationCriteria {

	@Override
	public List<Reservation> meetCriteria(List<Reservation> reservations) {
		List<Reservation> pastReservations = new ArrayList<>();
		DateTime now = new DateTime();
		for(Reservation reservation : reservations){
			if(reservation.getStartDate().isBefore(now)){
				pastReservations.add(reservation);
			}
		}	
		return pastReservations;
	}
}
