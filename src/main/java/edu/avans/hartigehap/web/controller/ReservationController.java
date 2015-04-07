package edu.avans.hartigehap.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.avans.hartigehap.domain.Customer;
import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.CustomerService;
import edu.avans.hartigehap.service.ReservationService;
import edu.avans.hartigehap.service.RestaurantService;

@Controller
public class ReservationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);
	

	@Autowired
	private ReservationService reservationService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private RestaurantService restaurantService;

	@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
	@RequestMapping(value = "/reservations", method = RequestMethod.GET)
	public String listReservations(Model uiModel) {
		
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		
		Collection<Reservation> reservations = reservationService.findAll();
		uiModel.addAttribute("reservations", reservations);
		
		return "hartigehap/listreservations";
	}
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
	@RequestMapping(value = "/reservations/{reservationID}", method = RequestMethod.GET)
	public String showReservation(@PathVariable("reservationID") Long reservationID, Model uiModel) {
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		
		Reservation reservation = reservationService.findById(reservationID);
		uiModel.addAttribute("reservation", reservation);
		
		return "hartigehap/reservation";
	}
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
	@RequestMapping(value = "/reservations/{reservationID}", method = RequestMethod.DELETE)
	public String deleteReservation(@PathVariable("reservationID") Long reservationID, Reservation reservation, Model uiModel){	
		Reservation existingReservation = reservationService.findById(reservationID);
        assert existingReservation != null : "reservation should exist";
		reservationService.delete(existingReservation);
        
        return "redirect:../reservations";
	}
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
	@RequestMapping(value = "/reservations/{reservationID}", method = RequestMethod.PUT)
	public String showReservation(@PathVariable("reservationID") Long reservationID, Reservation reservation, Model uiModel) {

		
		Reservation existingReservation = reservationService.findById(reservationID);
        assert existingReservation != null : "reservation should exist";

    	
    	reservation.setRestaurant(restaurantService.findById(reservation.getRestaurant().getId()));
    	
        if(existingReservation.getCustomer().getPartySize() != reservation.getCustomer().getPartySize() || !reservation.getStartDate().equals(existingReservation.getStartDate()) || !reservation.getEndDate().equals(existingReservation.getEndDate())){
        	DiningTable diningTable = checkReservation(reservation, (List<DiningTable>) reservation.getRestaurant().getDiningTablesBySeats(reservation.getCustomer().getPartySize()));
    		if(diningTable == null){
    			//geen andere tafel beschikbaar
    			LOGGER.info("No empty table found");
    			return "redirect:../reservations";
    		}else{
    			reservation.setDiningTable(diningTable);
    		}
        }
        
        // update user-editable fields
        existingReservation.updateEditableFields(reservation);

		reservationService.save(existingReservation);
		
		return "redirect:../reservations";
	}
	
	@RequestMapping(value = "/reservation", params = "form", method = RequestMethod.GET)
	public String createReservationForm(Model uiModel) {
		LOGGER.info("Create reservation form");
		Reservation reservation = new Reservation();
		uiModel.addAttribute("reservation", reservation);
		
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		uiModel.addAttribute("currentDate", format.format(new Date()));

		return "hartigehap/createreservationform";
	}
	
	@RequestMapping(value = "/reservation", params = "form", method = RequestMethod.POST)
	public String createReservation(@Valid Reservation reservation, BindingResult bindingResult,
			Model uiModel, HttpServletRequest httpServletRequest,
			RedirectAttributes redirectAttributes, Locale locale) throws MessagingException, IOException {
		LOGGER.info("Create reservation form");


		Restaurant restaurant = restaurantService.findById(reservation.getRestaurant().getId());
		DiningTable diningTable = checkReservation(reservation, (List<DiningTable>) restaurant.getDiningTablesBySeats(reservation.getCustomer().getPartySize()));
	
		if(diningTable == null){
			// geen plaats voor de reservering
			return "hartigehap/reservationfailed";
		} else {
			Customer customer = customerService.save(reservation.getCustomer());
			reservation.setCustomer(customer);

			reservation.setRestaurant(restaurant);
			restaurant.getReservations().add(reservation);
			reservation = reservationService.save(reservation);
			reservation.setDiningTable(diningTable);
			diningTable.getReservations().add(reservation);
			reservation.setCustomer(customer);
			customer.setReservation(reservation);
			reservation.setCode(randomGenerator());

			customer = customerService.save(customer);
			restaurant = restaurantService.save(restaurant);
			
			MailController.sendMail(reservation);
			
			uiModel.addAttribute("reservation", reservation);
			return "hartigehap/reservationsuccessful";
		}
	}
	
	public static DiningTable checkReservation(Reservation reservation, Collection<DiningTable> diningTables){
		for(DiningTable dt : diningTables){
			boolean freespot = true;
			for(Reservation r : dt.getReservationsByDate(reservation.getStartDate())){
				
				if((reservation.getStartDate().isBefore(r.getStartDate()) && reservation.getStartDate().isBefore(r.getEndDate())) && (reservation.getEndDate().isBefore(r.getStartDate()) && reservation.getEndDate().isBefore(r.getEndDate()))){
	                //afspraak voor de huidige
	            } else if((reservation.getStartDate().isAfter(r.getStartDate()) && reservation.getStartDate().isAfter(r.getEndDate())) && (reservation.getEndDate().isAfter(r.getStartDate()) && reservation.getEndDate().isAfter(r.getEndDate()))){
	                //afspraak na de hudige
	            } else if((reservation.getStartDate().equals(r.getEndDate()) && reservation.getEndDate().isAfter(r.getEndDate())) || (reservation.getEndDate().equals(r.getStartDate()) && reservation.getStartDate().isBefore(r.getStartDate()))){
	                //afspraak aansluitend aan huidige
	            } else {
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
	
	public static String randomGenerator(){
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		
		StringBuilder sb = new StringBuilder(6);
		for(int i = 0; i<6; i++){
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}
}




