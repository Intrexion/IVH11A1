package edu.avans.hartigehap.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.repository.ReservationRepository;
import edu.avans.hartigehap.service.ReservationService;

@Service("reservationService")
@Repository
@Transactional
public class ReservationServiceImpl implements ReservationService {
	final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

	@Autowired
	private ReservationRepository reservationRepository;
	@Override
	public List<Reservation> findAll() {
		return Lists.newArrayList(reservationRepository.findAll(new Sort(Sort.Direction.ASC, "id")));
	}

	@Override
	public Reservation findById(Long id) {
		return reservationRepository.findOne(id);
	}

	private DiningTable checkReservation(Reservation reservation, Collection<DiningTable> diningTables){
		for(DiningTable dt : diningTables){
			boolean freespot = true;
			for(Reservation r : dt.getReservationsByDate(reservation.getStartDate())){
				
				if((reservation.getStartDate().isBefore(r.getStartDate()) && reservation.getStartDate().isBefore(r.getEndDate())) && (reservation.getEndDate().isBefore(r.getStartDate()) && reservation.getEndDate().isBefore(r.getEndDate()))){
	                //afspraak voor de huidige
	            }
	            else if((reservation.getStartDate().isAfter(r.getStartDate()) && reservation.getStartDate().isAfter(r.getEndDate())) && (reservation.getEndDate().isAfter(r.getStartDate()) && reservation.getEndDate().isAfter(r.getEndDate()))){
	                //afspraak na de hudige
	            }
	            else if((reservation.getStartDate().equals(r.getEndDate()) && reservation.getEndDate().isAfter(r.getEndDate())) || (reservation.getEndDate().equals(r.getStartDate()) && reservation.getStartDate().isBefore(r.getStartDate()))){
	                //afspraak aansluitend aan huidige
	            }
	            else{
	                freespot = false;
	                break;
	            }
	        }
	        if(freespot){    
			return dt;
	        }
		}
		return null;
	}
	
	@Override
	public Reservation save(Reservation reservation) {
		return reservationRepository.save(reservation);
	}

	@Override
	public void delete(Reservation reservation) {
		reservationRepository.delete(reservation);		
	}

	@Override
	public List<Reservation> findByRestaurant(Restaurant restaurant) {
		return reservationRepository.findByRestaurant(restaurant, new Sort(Sort.Direction.ASC, "name"));
	}

}
