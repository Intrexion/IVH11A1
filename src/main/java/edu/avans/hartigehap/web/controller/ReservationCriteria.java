package edu.avans.hartigehap.web.controller;

import java.util.List;

import edu.avans.hartigehap.domain.Reservation;

public interface ReservationCriteria {
	public List<Reservation> meetCriteria(List<Reservation> reservations);
}
