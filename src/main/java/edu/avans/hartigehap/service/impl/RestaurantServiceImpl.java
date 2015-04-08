package edu.avans.hartigehap.service.impl;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.avans.hartigehap.aop.MyExecutionTime;
import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.repository.*;
import edu.avans.hartigehap.service.*;

import com.google.common.collect.Lists;

@Service("restaurantService")
@Repository
@Transactional
public class RestaurantServiceImpl implements RestaurantService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantServiceImpl.class);
	
	@Autowired
	private RestaurantRepository restaurantRepository;
		
	@MyExecutionTime
	@Transactional(readOnly=true)
	public List<Restaurant> findAll() {
		LOGGER.info("find all restaurants");
		// MySQL and H2 return the restaurants of findAll() in different order
		// sorting the result makes the behavior less database vendor dependent
		Sort sort = new Sort(Sort.Direction.ASC, "id");
		return Lists.newArrayList(restaurantRepository.findAll(sort));
	}

	@MyExecutionTime
	@Transactional(readOnly=true)
	public Restaurant findById(String restaurant) {
		LOGGER.info("find by id: " + restaurant);
		return restaurantRepository.findOne(restaurant);
	}
	
	@MyExecutionTime
	public Restaurant save(Restaurant restaurant) {
		LOGGER.info("save restaurant");
		return restaurantRepository.save(restaurant);
	}

	@MyExecutionTime
	public void delete(String restaurant) {
		LOGGER.info("delete restaurant: " + restaurant);
		restaurantRepository.delete(restaurant);
	}

	@MyExecutionTime
	@Transactional(readOnly=true)
	public Page<Restaurant> findAllByPage(Pageable pageable) {
		LOGGER.info("find all restaurants by page");
		return restaurantRepository.findAll(pageable);
	}

	/**
	 *  to be able to follow associations outside the context of a transaction, prefetch the associated
	 *  entities by traversing the associations
	 */
	@MyExecutionTime
	@Transactional(readOnly=true)
	public Restaurant fetchWarmedUp(String restaurantName) {
		LOGGER.info("fetchwarmedUp restaurant");
		Restaurant restaurant = restaurantRepository.findOne(restaurantName);
		restaurant.warmup();
		
		return restaurant;
	}

}
