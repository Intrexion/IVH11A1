package edu.avans.hartigehap.web.controller.rs;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.Reservation;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.ReservationService;
import edu.avans.hartigehap.service.RestaurantService;
import edu.avans.hartigehap.web.controller.ReservationController;

@Controller
public class ReservationsRS {
	private final Logger logger = LoggerFactory.getLogger(RestaurantsRS.class);


	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private RestaurantService restaurantService;
	
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
	
	@RequestMapping(value = RSConstants.URL_PREFIX + "/reservations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public HashMap<String, Object> createReservationJson(Reservation reservation, BindingResult bindingResult) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		
		Restaurant restaurant = restaurantService.findById(reservation.getRestaurant().getId());
		DiningTable diningTable = ReservationController.checkReservation(reservation, (List<DiningTable>) restaurant.getDiningTablesBySeats(reservation.getCustomer().getPartySize()));
		if(diningTable == null){
			response.put("result", "FAIL");
		}else{
			reservation.setRestaurant(restaurant);
			restaurant.getReservations().add(reservation);
			reservation.setDiningTable(diningTable);
			diningTable.getReservations().add(reservation);
			reservation.getCustomer().setReservation(reservation);
			
			reservation = reservationService.save(reservation);
			response.put("result", "OK");
		}
		response.put("reservation", reservation);
		return response;
	}
	
	@RequestMapping(value = RSConstants.URL_PREFIX + "/reservations/{reservationId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public void delete(@PathVariable Long reservationId, HttpServletResponse httpResponse, WebRequest httpRequest) {
		
		Reservation toBeDeleted = reservationService.findById(reservationId);
		
		logger.debug("reservationId: {}", toBeDeleted);
		reservationService.delete(toBeDeleted);
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
