package edu.avans.hartigehap.web.controller;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.service.*;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

import javax.annotation.PostConstruct;


@Controller
public class RestaurantController {
		
	final Logger logger = LoggerFactory.getLogger(RestaurantController.class);
	
	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private RestaurantPopulatorService restaurantPopulatorService;

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
		
		uiModel.addAttribute("reservations", getReservationsForHour(restaurant));
		
		restaurant.getDiningTables().removeAll(getOccupiedTables(restaurant));
		uiModel.addAttribute("restaurant", restaurant);
		
		
		
		return "hartigehap/restaurant";
	}
	
	private ArrayList<Reservation> getReservationsForHour(Restaurant restaurant){
		ArrayList<Reservation> reservations = new ArrayList<>();
		DateTime now = new DateTime();
		for(DiningTable dt : restaurant.getDiningTables()){
			for(Reservation reservation : dt.getReservationsByDate(now)){
				System.out.println(dt.getReservationsByDate(now).size());
				if(reservation.getStartDate().isAfter(now.minusMinutes(30)) && reservation.getStartDate().isBefore(now.plusMinutes(30))){
					reservations.add(reservation);
				}
			}
		}
		
		
		return reservations;
	}
	
	private ArrayList<DiningTable> getOccupiedTables(Restaurant restaurant){
		ArrayList<DiningTable> diningTables = new ArrayList<>();
		DateTime now = new DateTime();
		for(DiningTable dt : restaurant.getDiningTables()){
			for(Reservation r : dt.getReservationsByDate(now)){
				
				if(now.plusMinutes(30).isBefore(r.getStartDate())){
	                //afspraak voor de huidige
	            }
	            else if(now.isAfter(r.getEndDate())){
	                //afspraak na de hudige
	            }
	            else if(now.equals(r.getEndDate())){
	                //afspraak aansluitend aan huidige
	            }
	            else{
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
