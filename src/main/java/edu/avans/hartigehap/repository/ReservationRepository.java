package edu.avans.hartigehap.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.domain.DiningTable;

public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {
	List<Reservation> findByRestaurant(Restaurant restaurant, Sort sort);
	List<Reservation> findByDiningTable(DiningTable diningTable, Sort sort);
}
