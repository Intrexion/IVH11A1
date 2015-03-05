package edu.avans.hartigehap.web.controller;

import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.avans.hartigehap.domain.Customer;
import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.service.CustomerService;
import edu.avans.hartigehap.service.DiningTableService;
import edu.avans.hartigehap.service.ReservationService;

@Controller
public class ReservationController {
	final Logger logger = LoggerFactory.getLogger(ReservationController.class);
	@Autowired
	private DiningTableService diningTableService;	
	@Autowired
	private ReservationService reservationService;
	@Autowired
	private CustomerService customerService;
	
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
		
		return "hartigehap/createreservationform";
	}
	@RequestMapping(value = "/reservation", params = "form", method = RequestMethod.POST)
	public String createReservation(Reservation reservation, BindingResult bindingResult,
			Model uiModel, HttpServletRequest httpServletRequest,
			RedirectAttributes redirectAttributes, Locale locale) {
		logger.info("Create reservation form");
		
		
		Collection<DiningTable> diningTables = diningTableService.findbySeatsGreaterThanEqualAndRestaurant(reservation.getRestaurant(), reservation.getCustomer().getPartySize(), new Sort(Sort.Direction.ASC, "seats"));
		
		reservationService.save(reservation, diningTables);
		
		Customer customer = customerService.save(reservation.getCustomer());
		reservation.setCustomer(customer);
		System.out.println(reservation.getCustomer().getPartySize());
		return "hartigehap/createreservationform";
	}
}
