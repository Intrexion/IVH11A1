package edu.avans.hartigehap.service;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.avans.hartigehap.domain.Customer;
import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.testutil.AbstractTransactionRollbackTest;

public class ReservationServiceTest extends AbstractTransactionRollbackTest {
	private final String HARTIGEHAP_RESTAURANT_NAME = "HartigeHap";
	private final String FIRST_NAME = "Tom";
	private final String LAST_NAME = "Jaspers";
	@Autowired
	private ReservationService reservationService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private RestaurantService restaurantService;
	
	@Test
	public void created() {
		//Arrange
		Customer customer = createCustomer(FIRST_NAME, LAST_NAME);
		Restaurant restaurant = restaurantService.findById(HARTIGEHAP_RESTAURANT_NAME);
		List<DiningTable> tables = (List<DiningTable>) restaurant.getDiningTables();
		DiningTable diningTbl = tables.get(0);
		LocalTime start = LocalTime.now();
		LocalTime end = LocalTime.now();
		LocalDate day = LocalDate.now();
		
		Reservation reservation = createReservation(customer, restaurant, diningTbl, start, end, day);
		//Assert
		List<Reservation> reservations = reservationService.findAll();
		assertNotNull(reservations);
		assertTrue("created reservation in list", reservations.contains(reservation));
	}
	
	@Test
	public void findByReservation() {
		// prepare
		Customer customer = createCustomer(FIRST_NAME, LAST_NAME);
		Restaurant restaurant = restaurantService.findById(HARTIGEHAP_RESTAURANT_NAME);
		List<DiningTable> tables = (List<DiningTable>) restaurant.getDiningTables();
		DiningTable diningTbl = tables.get(0);
		LocalTime start = LocalTime.now();
		LocalTime end = LocalTime.now();
		LocalDate day = LocalDate.now();
		
		Reservation reservation = createReservation(customer, restaurant, diningTbl, start, end, day);

		// execute
		List<Reservation> reservation2 = reservationService.findByRestaurant(restaurant);

		// verify
		assertTrue("created customer in findByFirstNameAndLastName", reservation2.contains(reservation));
	}

	@Test
	public void delete() {
		//Arrange
		Customer customer = createCustomer(FIRST_NAME, LAST_NAME);
		Restaurant restaurant = restaurantService.findById(HARTIGEHAP_RESTAURANT_NAME);
		List<DiningTable> tables = (List<DiningTable>) restaurant.getDiningTables();
		DiningTable diningTbl = tables.get(0);
		LocalTime start = LocalTime.now();
		LocalTime end = LocalTime.now();
		LocalDate day = LocalDate.now();
		
		Reservation reservation = createReservation(customer, restaurant, diningTbl, start, end, day);
		// execute
		reservationService.delete(reservation);

		// verify
		List<Reservation> reservations2 = reservationService.findAll();
		assertNotNull(reservations2);
		assertFalse("deleted customer not in the list", reservations2.contains(reservation));
	}


	private Reservation createReservation(Customer customer,
			Restaurant restaurant, DiningTable diningTbl, LocalTime start,
			LocalTime end, LocalDate day) {
		Reservation reservation = new Reservation(start, end, day, "Test");
		reservation.setCustomer(customer);
		reservation.setRestaurant(restaurant);
		reservation.setDiningTable(diningTbl);
		Reservation retval = reservationService.save(reservation);
		assertNotNull(retval);
		assertNotNull(retval.getId());
		assertEquals("Customer", customer.getId(), retval.getCustomer().getId());
		assertEquals("Restaurant", restaurant.getId(), retval.getRestaurant().getId());
		assertEquals("DiningTable", diningTbl.getId(), retval.getDiningTable().getId());
		assertEquals("Start", start, retval.getStartTime());
		assertEquals("End", end, retval.getEndTime());
		assertEquals("Day", day, retval.getDay());
		return retval;
	}

	private Customer createCustomer(String firstName, String lastName) {
		Customer customer = new Customer();
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		Customer retval = customerService.save(customer);
		assertNotNull(retval);
		assertNotNull(retval.getId());
		assertEquals("firstName", firstName, retval.getFirstName());
		assertEquals("lastName", lastName, retval.getLastName());
		return retval;
	}
	
}
