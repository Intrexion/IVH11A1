package edu.avans.hartigehap.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import edu.avans.hartigehap.model.ReservationModel;
import edu.avans.hartigehap.service.CustomerService;
import edu.avans.hartigehap.service.DiningTableService;
import edu.avans.hartigehap.service.ReservationService;
import edu.avans.hartigehap.service.RestaurantService;
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
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		
		Reservation reservation = reservationService.findById(reservationID);
		uiModel.addAttribute("reservation", reservation);
		
		return "hartigehap/reservation";
	}
	
	@RequestMapping(value = "/reservations/{reservationID}", method = RequestMethod.DELETE)
	public String deleteReservation(@PathVariable("reservationID") Long reservationID, Reservation reservation, Model uiModel){	
		Reservation existingReservation = reservationService.findById(reservationID);
        assert existingReservation != null : "reservation should exist";
		reservationService.delete(existingReservation);
        
        return "redirect:../reservations";
	}
	
	@RequestMapping(value = "/reservations/{reservationID}", method = RequestMethod.PUT)
	public String showReservation(@PathVariable("reservationID") Long reservationID, Reservation reservation, Model uiModel) {

		
		Reservation existingReservation = reservationService.findById(reservationID);
        assert existingReservation != null : "reservation should exist";
        
    	DateTimeProvider provider = new DateTimeAdapter(new DateAndTime(reservation.getDay(), reservation.getStartTime()));
		reservation.setStartDate(provider.getDateTime());
    	provider = new DateTimeAdapter(new DateAndTime(reservation.getDay(), reservation.getEndTime()));
    	reservation.setEndDate(provider.getDateTime());
    	
    	
    	System.out.println(reservation.getCustomer().getPartySize());
    	System.out.println(existingReservation.getCustomer().getPartySize());    	
    	System.out.println(reservation.getStartDate());
    	System.out.println(existingReservation.getStartDate());
    	System.out.println(reservation.getEndDate());
    	System.out.println(existingReservation.getEndDate());
    	
    	reservation.setRestaurant(restaurantService.findById(reservation.getRestaurant().getId()));
    	
        if(existingReservation.getCustomer().getPartySize() != reservation.getCustomer().getPartySize() || !reservation.getStartDate().equals(existingReservation.getStartDate()) || !reservation.getEndDate().equals(existingReservation.getEndDate())){
    		System.out.println(reservation.getRestaurant().getDiningTablesBySeats(reservation.getCustomer().getPartySize()).size());
        	DiningTable diningTable = checkReservation(reservation, (List<DiningTable>) reservation.getRestaurant().getDiningTablesBySeats(reservation.getCustomer().getPartySize()));
    		if(diningTable ==null){
    			//geen andere tafel beschikbaar
    			//TODO hier alles afbreken
    			System.out.println("geen dt gevonden");
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
		logger.info("Create reservation form");
		ReservationModel model = new ReservationModel();
		uiModel.addAttribute("reservationmodel", model);
		
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		uiModel.addAttribute("currentDate", format.format(new Date()));

		return "hartigehap/createreservationform";
	}
	
	@RequestMapping(value = "/reservation", params = "form", method = RequestMethod.POST)
	public String createReservation(@Valid ReservationModel model, BindingResult bindingResult,
			Model uiModel, HttpServletRequest httpServletRequest,
			RedirectAttributes redirectAttributes, Locale locale) {
		logger.info("Create reservation form");
		
		Reservation reservation = new Reservation();
		DateTimeAdapter dateTimeAdapter = new DateTimeAdapter();

		DateAndTime dateAndTimeStart = new DateAndTime(model.getDate(), model.getStartTime());
		dateTimeAdapter.setDateAndTime(dateAndTimeStart);
		DateTime startDate = dateTimeAdapter.getDateTime();
		reservation.setStartDate(startDate);

		DateAndTime dateAndTimeEnd = new DateAndTime(model.getDate(), model.getEndTime());
		dateTimeAdapter.setDateAndTime(dateAndTimeEnd);
		DateTime endDate = dateTimeAdapter.getDateTime();
		reservation.setEndDate(endDate);
		reservation.setStartTime(model.getStartTime());
		reservation.setEndTime(model.getEndTime());
		reservation.setDay(model.getDate());
		
		reservation.setDescription(model.getDescription());

		Customer customer = new Customer.Builder(model.getFirstName(), model.getLastName())
				.setPartySize(model.getPartySize())
				.setEmail(model.getEmail())
				.setPhone(model.getPhone())
				.build();

		Restaurant restaurant = restaurantService.findById(model.getRestaurantId());
		DiningTable diningTable = checkReservation(reservation, (List<DiningTable>) restaurant.getDiningTablesBySeats(customer.getPartySize()));
	
		if(diningTable == null){
			// geen plaats voor de reservering
			return "hartigehap/reservationfailed";
		} else {
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

			return "hartigehap/reservationsuccessful";
		}
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
