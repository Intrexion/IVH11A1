package edu.avans.hartigehap.web.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import edu.avans.hartigehap.domain.Customer;
import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.DomainObject;
import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.model.ReservationModel;
import edu.avans.hartigehap.service.CustomerService;
import edu.avans.hartigehap.service.DiningTableService;
import edu.avans.hartigehap.service.ReservationService;
import edu.avans.hartigehap.service.RestaurantService;

import org.mockito.Matchers;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ReservationControllerTest.class })
@WebAppConfiguration
@ImportResource({ "classpath:/test-root-context.xml",
		"classpath:*servlet-context.xml" })
@Slf4j
@EnableWebMvc
public class ReservationControllerTest {
	private static final String RESTAURANT_ID = "De Plak";
	@Autowired
	private ReservationController reservationController;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	
	@Autowired
	private ReservationService reservationServiceMock;

	@Autowired
	private RestaurantService restaurantServiceMock;

	@Autowired
	private CustomerService customerServiceMock;

	@Autowired
	private DiningTableService diningTableServiceMock;

	@Before
	public void setUp() {
		// Thanks to: Petri Kinulainen:
		// http://www.petrikainulainen.net/programming/spring-framework/unit-testing-of-spring-mvc-controllers-normal-controllers/
		// https://github.com/pkainulainen/spring-mvc-test-examples/tree/master/controllers-unittest

		// We have to reset our mock between tests because the mock objects
		// are managed by the Spring container. If we would not reset them,
		// stubbing and verified behavior would "leak" from one test to another.
		Mockito.reset(reservationServiceMock);
		Mockito.reset(restaurantServiceMock);
		Mockito.reset(customerServiceMock);
		Mockito.reset(diningTableServiceMock);
		
		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				  .build();
	}

	@Bean
	public ReservationService reservationService() {
		return Mockito.mock(ReservationService.class);
	}

	@Bean
	public RestaurantService restaurantService() {
		return Mockito.mock(RestaurantService.class);
	}

	@Bean
	public CustomerService customerService() {
		return Mockito.mock(CustomerService.class);
	}

	@Bean
	public DiningTableService diningTableService() {
		return Mockito.mock(DiningTableService.class);
	}

	@Test
	public void dummy() {
		log.debug("test the configuration of the test case, 'the wiring'");
		assertNotNull(reservationController);
		Object reservationServiceMock = ReflectionTestUtils.getField(reservationController, "reservationService");
		assertTrue(reservationServiceMock instanceof ReservationService);
		String className = reservationServiceMock.getClass().getName();
		log.debug("className: {}", className);
		assertTrue("classname contains 'Mock' since it is a mockito mock", className.indexOf("Mock") >= 0);
	}

	@Test
	public void reservations() throws Exception {
		List<Reservation> reservations = getReservations();
		Mockito.when(reservationServiceMock.findAll()).thenReturn(reservations);
		mockMvc.perform(get("/reservations"))
				.andExpect(status().isOk())
				.andExpect(view().name("hartigehap/listreservations"))
				.andExpect(model().attribute("reservations", hasItems(reservations.toArray(new Reservation[] {}))));
	}

	@Test
	public void createReservationForm() throws Exception {
		List<Restaurant> restaurants = getRestaurants();

		Mockito.when(restaurantServiceMock.findAll()).thenReturn(restaurants);
		mockMvc.perform(get("/reservation").param("form", ""))
				.andExpect(view().name("hartigehap/createreservationform"))
				.andExpect(model().attributeExists("restaurants"))
				.andExpect(model().attributeExists("currentDate"))
				.andExpect(model().attributeExists("reservationmodel"));
	}

	@Test
	public void deleteReservation() throws Exception{
		Reservation reservation = new Reservation();
		reservation.setId(1L);
		reservation.setStartDate(new DateTime());
		reservation.setEndDate(new DateTime());
		reservation.setCustomer(new Customer.Builder("Henk", "Jaspers").setPartySize(2).build());
		
		Mockito.when(reservationServiceMock.findById(1L)).thenReturn(reservation);
		mockMvc.perform(delete("/reservations/{reservationID}", 1L))
						.andExpect(status().isFound())
						.andExpect(view().name("redirect:../reservations"));
	}
	
	@Test
	public void updateReservation() throws Exception{
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Reservation reservation = new Reservation();
		reservation.setId(1L);
		reservation.setStartDate(new DateTime());
		reservation.setEndDate(new DateTime());
		reservation.setCustomer(new Customer.Builder("Henk", "Jaspers").setPartySize(2).build());
		
		
		Mockito.when(reservationServiceMock.findAll()).thenReturn(getReservations());
		Mockito.when(diningTableServiceMock.findAll()).thenReturn(getDiningTables());
		Mockito.when(restaurantServiceMock.findById(RESTAURANT_ID)).thenReturn(getRestaurant());
		Mockito.when(reservationServiceMock.findById(1L)).thenReturn(reservation);
		
		Mockito.when(reservationServiceMock.save(Matchers.any(Reservation.class))).thenAnswer(domainObjectAnswer);
		Mockito.when(restaurantServiceMock.save(Matchers.any(Restaurant.class))).thenAnswer(restaurantAnswer);
		Mockito.when(diningTableServiceMock.save(Matchers.any(DiningTable.class))).thenAnswer(domainObjectAnswer);
		Mockito.when(customerServiceMock.save(Matchers.any(Customer.class))).thenAnswer(domainObjectAnswer);
		//execute
		mockMvc.perform(put("/reservations/{reservationID}", 1L)
											.contentType(MediaType.APPLICATION_FORM_URLENCODED)
											.param("id", "1")
											.param("restaurant.id", RESTAURANT_ID)
											.param("customer.firstName", "Henk")
											.param("customer.lastName", "Jaspers")
											.param("day", format.format(new Date()))
											.param("startTime", "14:11")
											.param("endTime", "18:11")
											.param("customer.id", "1")
											.param("customer.email", "tom@tom.nl")
											.param("customer.partySize", "1")
											.param("customer.phone", "1234567890")
											.param("description", "Test")
											.sessionAttr("reservation", new Reservation()))
		.andExpect(view().name("redirect:../reservations"));
	}
	
	@Test
	public void createReservationSuccess() throws Exception {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Reservation reservation = new Reservation();
		reservation.setId(2L);
		
		Mockito.when(reservationServiceMock.findAll()).thenReturn(getReservations());
		Mockito.when(diningTableServiceMock.findAll()).thenReturn(getDiningTables());
		Mockito.when(restaurantServiceMock.findById(RESTAURANT_ID)).thenReturn(getRestaurant());
		Mockito.when(reservationServiceMock.save(Matchers.any(Reservation.class))).thenAnswer(domainObjectAnswer);
		Mockito.when(restaurantServiceMock.save(Matchers.any(Restaurant.class))).thenAnswer(restaurantAnswer);
		Mockito.when(diningTableServiceMock.save(Matchers.any(DiningTable.class))).thenAnswer(domainObjectAnswer);
		Mockito.when(customerServiceMock.save(Matchers.any(Customer.class))).thenAnswer(domainObjectAnswer);
		
		//execute
		mockMvc.perform(post("/reservation").param("form", "")
											.contentType(MediaType.APPLICATION_FORM_URLENCODED)
											.param("restaurantId", RESTAURANT_ID)
											.param("firstName", "Henk")
											.param("lastName", "Jaspers")
											.param("date", format.format(new Date()))
											.param("startTime", "13:11")
											.param("endTime", "15:11")
											.param("email", "tom@tom.nl")
											.param("partySize", "1")
											.param("phone", "1234567890")
											.param("description", "Test")
											.sessionAttr("model", new ReservationModel()))
		.andExpect(view().name("hartigehap/reservationsuccessful"));
	}

	@Test
	public void createReservationFailed() throws Exception {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Reservation reservation = new Reservation();
		reservation.setId(2L);
		
		Mockito.when(reservationServiceMock.findAll()).thenReturn(getReservations());
		Mockito.when(diningTableServiceMock.findAll()).thenReturn(getDiningTables());
		Mockito.when(restaurantServiceMock.findById(RESTAURANT_ID)).thenReturn(getRestaurant());

		Mockito.when(reservationServiceMock.save(Matchers.any(Reservation.class))).thenAnswer(domainObjectAnswer);
		Mockito.when(restaurantServiceMock.save(Matchers.any(Restaurant.class))).thenAnswer(restaurantAnswer);
		Mockito.when(diningTableServiceMock.save(Matchers.any(DiningTable.class))).thenAnswer(domainObjectAnswer);
		Mockito.when(customerServiceMock.save(Matchers.any(Customer.class))).thenAnswer(domainObjectAnswer);
		//execute
		mockMvc.perform(post("/reservation").param("form", "")
											.contentType(MediaType.APPLICATION_FORM_URLENCODED)
											.param("restaurantId", RESTAURANT_ID)
											.param("firstName", "Henk")
											.param("lastName", "Jaspers")
											.param("date", format.format(new Date()))
											.param("startTime", "13:11")
											.param("endTime", "15:11")
											.param("email", "tom@tom.nl")
											.param("partySize", "111")
											.param("phone", "1234567890")
											.param("description", "Test")
											.sessionAttr("model", new ReservationModel()))
		.andExpect(view().name("hartigehap/reservationfailed"));
	}
	
	private List<Reservation> getReservations() {
		LinkedList<Reservation> retval = new LinkedList<Reservation>();
		Reservation r1 = new Reservation();
		r1.setId(1l);
		retval.add(r1);
		return retval;
	}

	private List<DiningTable> getDiningTables() {
		LinkedList<DiningTable> retval = new LinkedList<DiningTable>();
		for (int i = 0; i < 4; i++) {
			DiningTable d1 = new DiningTable();
			d1.setId((long) i);
			d1.setSeats(i + 2);
			retval.add(d1);
		}
		return retval;
	}

	private Restaurant getRestaurant(){
		return getRestaurants().get(0);
	}
	
	private Answer<DomainObject> domainObjectAnswer = new Answer<DomainObject>(){
		@Override
		public DomainObject answer(InvocationOnMock invocation) throws Throwable {
			DomainObject obj = (DomainObject) invocation.getArguments()[0];
			obj.setId(1L);
			return obj;
		}
	};
	
	private Answer<Restaurant> restaurantAnswer = new Answer<Restaurant>(){
		@Override
		public Restaurant answer(InvocationOnMock invocation) throws Throwable {
			Restaurant restaurant = (Restaurant) invocation.getArguments()[0];
			restaurant.setId(RESTAURANT_ID);
			return restaurant;
		}
	};
	
	private List<Restaurant> getRestaurants() {
		LinkedList<Restaurant> retval = new LinkedList<Restaurant>();
		Restaurant r1 = new Restaurant();
		r1.setId(RESTAURANT_ID);
		List<DiningTable> diningTables = getDiningTables();
		for(DiningTable d : diningTables)
			d.setRestaurant(r1);
		r1.setDiningTables(diningTables);
		retval.add(r1);
		return retval;
	}
}
