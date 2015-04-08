package edu.avans.hartigehap.domain;

import java.util.List;

public interface ReservationCriteria {
	public List<Reservation> meetCriteria(List<Reservation> reservations);
}
