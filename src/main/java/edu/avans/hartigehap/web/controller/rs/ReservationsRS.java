package edu.avans.hartigehap.web.controller.rs;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.ReservationService;
import edu.avans.hartigehap.service.RestaurantService;

@Controller
public class ReservationsRS {
	private final Logger logger = LoggerFactory.getLogger(RestaurantsRS.class);


	@Autowired
	private ReservationService reservationService;
	
	private View jsonView = null;

	private static final String DATA_FIELD = "data";
	private static final String ERROR_FIELD = "error";
	
	/**
	 * list all restaurants.
	 * 
	 * @return
	 */
	@RequestMapping(value = RSConstants.URL_PREFIX + "/reservations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Reservation> reservations() {
		logger.debug("");
		return reservationService.findAll();
	}
	
	@RequestMapping(value = RSConstants.URL_PREFIX + "/reservations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView createReservationJson(@RequestBody Reservation reservation, HttpServletResponse httpResponse,
	        WebRequest httpRequest) {
		logger.debug("body: {}", reservation);

		try {
			Reservation savedReservation = reservationService.save(reservation);
			httpResponse.setStatus(HttpStatus.CREATED.value());
			httpResponse
			        .setHeader("Location", httpRequest.getContextPath() + "/reservations/" + savedReservation.getId());
			return new ModelAndView(jsonView, DATA_FIELD, savedReservation);
		} catch (Exception e) {
			logger.error("Error creating new restaurant", e);
			String message = "Error creating new restaurant. [%1$s]";
			return createErrorResponse(String.format(message, e.toString()));
		}
	}
	
	@RequestMapping(value = RSConstants.URL_PREFIX + "/reservations/{reservationId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public void delete(@PathVariable Reservation reservation, HttpServletResponse httpResponse, WebRequest httpRequest) {
		logger.debug("reservationId: {}", reservation);
		reservationService.delete(reservation);
	}
	
	@RequestMapping(value = RSConstants.URL_PREFIX + "/reservations/{reservationId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public void update(@PathVariable Reservation reservation, HttpServletResponse httpResponse, WebRequest httpRequest) {
		logger.debug("reservationId: {}", reservation);
		reservationService.save(reservation);
	}	
	
	@RequestMapping(value = RSConstants.URL_PREFIX + "/reservations/{reservationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Reservation findById(@PathVariable Long reservationId, HttpServletResponse httpResponse, WebRequest httpRequest) {
		logger.debug("reservationId: {}", reservationId);
		return reservationService.findById(reservationId);
	}
	
	private ModelAndView createErrorResponse(String sMessage) {
		return new ModelAndView(jsonView, ERROR_FIELD, sMessage);
	}

	public void setRestaurantService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	public void setJsonView(View jsonView) {
		this.jsonView = jsonView;
	}
	
}
