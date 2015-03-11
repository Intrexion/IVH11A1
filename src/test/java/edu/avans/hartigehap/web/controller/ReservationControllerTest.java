package edu.avans.hartigehap.web.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.model.ReservationModel;
import edu.avans.hartigehap.service.CustomerService;
import edu.avans.hartigehap.service.DiningTableService;
import edu.avans.hartigehap.service.ReservationService;
import edu.avans.hartigehap.service.RestaurantService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ReservationControllerTest.class })
@WebAppConfiguration
@ImportResource({ "classpath:/test-root-context.xml",
		"classpath:*servlet-context.xml" })
@Slf4j
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
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Mockito.when(restaurantServiceMock.findAll()).thenReturn(restaurants);
		mockMvc.perform(get("/reservation").param("form", ""))
				.andExpect(view().name("hartigehap/createreservationform"))
				.andExpect(model().attribute("restaurants",	hasItems(restaurants.toArray(new Restaurant[] {}))))
				.andExpect(model().attribute("currentDate",	format.format(new Date())))
				.andExpect(model().attributeExists("reservationmodel"));
	}

	@Test
	public void createReservation() throws Exception {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List<Restaurant> restaurants = getRestaurants();
		ReservationModel model = new ReservationModel();
		model.setRestaurantId(RESTAURANT_ID);
		model.setDate(format.format(new Date()));
		model.setFirstName("Henk");
		model.setLastName("Jaspers");
		model.setStartTime("12:11");
		model.setEndTime("15:11");
		model.setEmail("tom@tom.nl");
		model.setPartySize(2);
		model.setPhone("1234567890");
		model.setDescription("Test");

		Mockito.when(restaurantServiceMock.findAll()).thenReturn(restaurants);
		mockMvc.perform(post("/reservation")
						.param("form", "")
						.sessionAttr("model",	model))
						.andExpect(view().name("hartigehap/reservationsuccessful"));
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

	private List<Restaurant> getRestaurants() {
		LinkedList<Restaurant> retval = new LinkedList<Restaurant>();
		Restaurant r1 = new Restaurant();
		r1.setId(RESTAURANT_ID);
		r1.setDiningTables(getDiningTables());
		retval.add(r1);
		return retval;
	}
}
