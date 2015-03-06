package edu.avans.hartigehap.service;

import java.util.List;

import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;

public interface ReservationService {
	List<Reservation> findAll();
	Reservation findById(Long id);
	Reservation save(Reservation reservation);
	void delete(Reservation owner);
	List<Reservation> findByRestaurant(Restaurant restaurant);
}
