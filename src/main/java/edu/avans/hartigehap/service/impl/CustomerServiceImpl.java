package edu.avans.hartigehap.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.avans.hartigehap.aop.MyExecutionTime;
import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.repository.*;
import edu.avans.hartigehap.service.*;

import com.google.common.collect.Lists;



@Service("customerService")
@Repository
@Transactional
public class CustomerServiceImpl implements CustomerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Autowired
	private CustomerRepository customerRepository;

	@Transactional(readOnly=true)
	public List<Customer> findAll() {
		LOGGER.info("Find all customers");
		return Lists.newArrayList(customerRepository.findAll());
	}
	
	@MyExecutionTime
	@Transactional(readOnly=true)
	public Customer findById(Long id) {
		LOGGER.info("Find customer by id" + id);
		return customerRepository.findOne(id);
	}

	@MyExecutionTime
	@Transactional(readOnly=true)
	public Customer findByFirstNameAndLastName(String firstName, String lastName) {
		LOGGER.info("Find by firstName: " + firstName + " and lastName: " + lastName );
		Customer customer = null;
		
		List<Customer> customers = customerRepository.findByFirstNameAndLastName(firstName, lastName);
		if (!customers.isEmpty()) {
			customer = customers.get(0);
		}
		return customer;
	}

	@MyExecutionTime
	@Transactional(readOnly=true)
	public List<Customer> findCustomersForRestaurant(Restaurant restaurant) {
		LOGGER.info("Find by customers by restaurant: " + restaurant.getId());
		// a query created using a repository method name
		List<Customer> customersForRestaurants = customerRepository.
				findByRestaurants(
						Arrays.asList(new Restaurant[]{restaurant}),
						new Sort(Sort.Direction.ASC, "lastName"));

		LOGGER.info("findCustomersForRestaurant using query created using repository method name");
		ListIterator<Customer>	it = customersForRestaurants.listIterator();
		while(it.hasNext()) {
			Customer customer = it.next();
			LOGGER.info("customer = " + customer);
		}
		
		return customersForRestaurants;
	}
	
	@MyExecutionTime
	@Transactional(readOnly=true)
	public Page<Customer> findAllByPage(Pageable pageable) {
		LOGGER.info("Find all customers (pageable)");
		return customerRepository.findAll(pageable);
	}	

	@MyExecutionTime
	@Transactional(readOnly=true)
	public Page<Customer> findCustomersForRestaurantByPage(Restaurant restaurant, Pageable pageable) {
		LOGGER.info("Find customers by restaurant" + restaurant.getId());
		// a query created using a repository method name
		Page<Customer> customersForRestaurants = customerRepository.
				findByRestaurants((Collection<Restaurant>)Arrays.asList(new Restaurant[]{restaurant}), pageable);

		LOGGER.info("findCustomersForRestaurant using query created using repository method name");
		Iterator<Customer>	it = customersForRestaurants.iterator();
		while(it.hasNext()) {
			Customer customer = it.next();
			LOGGER.info("customer = " + customer);
		}
		
		return customersForRestaurants;
	}	

	@MyExecutionTime
	public Customer save(Customer customer) {
		LOGGER.info("save customer" + customer.getFirstName() + customer.getLastName());
		return customerRepository.save(customer);
	}
	
	@MyExecutionTime
	public void delete(Long id) {
		LOGGER.info("delete customer: " + id);
		customerRepository.delete(id);
	}

	
}

