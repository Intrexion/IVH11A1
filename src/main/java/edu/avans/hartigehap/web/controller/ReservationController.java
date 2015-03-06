package edu.avans.hartigehap.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.avans.hartigehap.domain.Customer;
import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.CustomerService;
import edu.avans.hartigehap.service.DiningTableService;
import edu.avans.hartigehap.service.ReservationService;
import edu.avans.hartigehap.service.RestaurantService;
import edu.avans.hartigehap.web.form.Message;
import edu.avans.hartigehap.web.util.UrlUtil;

@Controller
public class ReservationController {
	final Logger logger = LoggerFactory.getLogger(ReservationController.class);
	
	@Autowired
	private ReservationService reservationService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private DiningTableService diningTableService;
	
	@RequestMapping(value = "/reservations", method = RequestMethod.GET)
	public String listReservations(Model uiModel) {
		Collection<Reservation> reservations = reservationService.findAll();
		uiModel.addAttribute("reservations", reservations);
		
		return "hartigehap/listreservations";
	}
	
	@RequestMapping(value = "/reservations/{reservationID}", method = RequestMethod.GET)
	public String showReservation(@PathVariable("reservationID") Long reservationID, Model uiModel) {
		Collection<Reservation> reservations = reservationService.findAll();
		uiModel.addAttribute("reservations", reservations);
		
		Reservation reservation = reservationService.findById(reservationID);
		uiModel.addAttribute("reservation", reservation);
		
		return "hartigehap/reservation";
	}
	@RequestMapping(value = "/reservation", params = "form", method = RequestMethod.GET)
	public String createReservationForm(Model uiModel) {
		logger.info("Create reservation form");
		Reservation reservation = new Reservation();
		reservation.setCustomer(new Customer());
		uiModel.addAttribute("reservation", reservation);
		
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		
		return "hartigehap/createreservationform";
	}
	@RequestMapping(value = "/reservation", params = "form", method = RequestMethod.POST)
	public String createReservation(Reservation reservation, BindingResult bindingResult,
			Model uiModel, HttpServletRequest httpServletRequest,
			RedirectAttributes redirectAttributes, Locale locale) {
		logger.info("Create reservation form");
		
		reservation.setStartDate(DateTimeConverter.stringToDateTime(reservation.getDay().toString(), reservation.getStartTime()));
		reservation.setEndDate(DateTimeConverter.stringToDateTime(reservation.getDay().toString(), reservation.getEndTime()));
		
		Customer formCust = reservation.getCustomer();
		
		Customer customer = new Customer.Builder(formCust.getFirstName(), formCust.getLastName())
												.setPartySize(formCust.getPartySize())
												.setEmail(formCust.getEmail())
												.setPhone(formCust.getPhone())
												.build();

		Restaurant restaurant = restaurantService.findById(reservation.getRestaurant().getId());
//		List<DiningTable> tables = (List<DiningTable>) restaurant.getDiningTables();
		DiningTable diningTable = checkReservation(reservation, (List<DiningTable>) restaurant.getDiningTablesBySeats(customer.getPartySize()));
//				diningTableService.findbySeatsGreaterThanEqualAndRestaurant(restaurant, reservation.getCustomer().getPartySize(), new Sort(Sort.Direction.ASC, "seats")));
	
		if(diningTable == null){
			// geen plaats voor de reservering
		}else{
		
		customer = customerService.save(customer);
		reservation.setCustomer(customer);
			
		reservation.setRestaurant(restaurant);
		restaurant.getReservations().add(reservation);
		reservation = reservationService.save(reservation);
		reservation.setDiningTable(diningTable);
		diningTable.getReservations().add(reservation);
		reservation.setCustomer(customer);
		customer.setReservation(reservation);
		
		customer = customerService.save(customer);
		restaurant = restaurantService.save(restaurant);
		}
		return "hartigehap/listrestaurants";
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
}
