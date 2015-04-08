package edu.avans.hartigehap.web.controller;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.service.*;
import edu.avans.hartigehap.web.form.Message;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;


@Controller
public class RestaurantController {
		
	private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);
	
	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private RestaurantPopulatorService restaurantPopulatorService;
	@Autowired
	private ReservationService reservationService;
	@Autowired
	private MessageSource messageSource;

	// mapping to "/" is not RESTful, but is for bootstrapping!
	@RequestMapping(value = {"/", "/restaurants"}, method = RequestMethod.GET)
	public String listRestaurants(Model uiModel) {
		
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);

		// use HartigeHap as default restaurant
		Restaurant restaurant = restaurantService.fetchWarmedUp(RestaurantPopulatorService.HARTIGEHAP_RESTAURANT_NAME);
		uiModel.addAttribute("restaurant", restaurant);
		
		
		return "hartigehap/listrestaurants";
	}


	@RequestMapping(value = "/restaurants/{restaurantName}", method = RequestMethod.GET)
	public String showRestaurant(@PathVariable("restaurantName") String restaurantName, Model uiModel) {

		// warmup stuff
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		
		Restaurant restaurant = restaurantService.fetchWarmedUp(restaurantName);
		
		ReservationCriteria nowReservations = new ReservationCriteriaNow();		
		List<Reservation> reservations = getReservationsByRestaurant(restaurant);
		
		uiModel.addAttribute("reservations", nowReservations.meetCriteria(reservations));
		
		restaurant.getDiningTables().removeAll(getOccupiedTables(restaurant));
		uiModel.addAttribute("restaurant", restaurant);
		
		
		
		return "hartigehap/restaurant";
	}
	
	@RequestMapping(value="/checkCode/{reservationid}/{code}", method = RequestMethod.GET)
	public String checkCode(@PathVariable("reservationid") String reservationId,@PathVariable("code") String code, HttpServletRequest request, RedirectAttributes redir, Locale locale) {
		Reservation reservation = reservationService.findById(Long.valueOf(reservationId));		
		if(reservation.getCode().equalsIgnoreCase(code)){
			return "redirect:/diningTables/" + reservation.getDiningTable().getId();
		}

		redir.addFlashAttribute("message", new Message("error", messageSource.getMessage("message_code_incorrect", new Object[]{}, locale))); 
		return "redirect:/restaurants/" + reservation.getRestaurant().getId();
	}
	
	private List<Reservation> getReservationsByRestaurant(Restaurant restaurant){
		List<Reservation> reservations = new ArrayList<>();
		for(DiningTable dt : restaurant.getDiningTables()){
			for(Reservation reservation : dt.getReservations()){
				reservations.add(reservation);
			}
		}
		return reservations;
	}
	
	private List<DiningTable> getOccupiedTables(Restaurant restaurant){
		List<DiningTable> diningTables = new ArrayList<>();
		DateTime now = new DateTime();
		for(DiningTable dt : restaurant.getDiningTables()) {
			for(Reservation r : dt.getReservationsByDate(now)){
				if(now.plusHours(1).isBefore(r.getStartDate())) {
					//afspraak voor de huidige
				} else if(now.isAfter(r.getEndDate())) {
					//afspraak na de hudige
				} else if(now.equals(r.getEndDate())) {
					//afspraak aansluitend aan huidige
				} else {
					diningTables.add(dt);
				}
			}
		}
		return diningTables;
	}
	
	// called once immediately after bean creation
	@PostConstruct
	public void createRestaurants() {
		restaurantPopulatorService.createRestaurantsWithInventory();
	}
	
}
