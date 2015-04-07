package edu.avans.hartigehap.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.repository.ReservationRepository;
import edu.avans.hartigehap.service.ReservationService;

@Service("reservationService")
@Repository
@Transactional
public class ReservationServiceImpl implements ReservationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);

	@Autowired
	private ReservationRepository reservationRepository;
	@Override
	public List<Reservation> findAll() {
		LOGGER.info("Find all reservations");
		return Lists.newArrayList(reservationRepository.findAll(new Sort(Sort.Direction.ASC, "id")));
	}

	@Override
	public Reservation findById(Long id) {
		LOGGER.info("find reservation by id: " + id);
		return reservationRepository.findOne(id);
	}
	
	@Override
	public Reservation save(Reservation reservation) {
		LOGGER.info("Save reservation");
		return reservationRepository.save(reservation);
	}

	@Override
	public void delete(Reservation reservation) {
		LOGGER.info("delete reservation " + reservation.getId());
		reservationRepository.delete(reservation);		
	}

	@Override
	public List<Reservation> findByRestaurant(Restaurant restaurant) {
		LOGGER.info("find reservation by restaurant " + restaurant.getId());
		return reservationRepository.findByRestaurant(restaurant, new Sort(Sort.Direction.ASC, "id"));
	}

}
