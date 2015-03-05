package edu.avans.hartigehap.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.avans.hartigehap.domain.Customer;
import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.ReservationService;
import edu.avans.hartigehap.web.form.Message;
import edu.avans.hartigehap.web.util.UrlUtil;

@Controller
public class ReservationController {
	final Logger logger = LoggerFactory.getLogger(ReservationController.class);
	@Autowired
	private ReservationService reservationService;
	
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
		
		uiModel.addAttribute("reservation", new Reservation());
		
		return "hartigehap/createreservationform";
	}
}
