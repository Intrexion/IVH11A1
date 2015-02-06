package edu.avans.hartigehap.web.controller;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.service.*;


@Controller
@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
public class WaiterController {

	final Logger logger = LoggerFactory.getLogger(WaiterController.class);

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private BillService billService;
	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/restaurants/{restaurantName}/waiter", method = RequestMethod.GET)
	public String showWaiter(@PathVariable("restaurantName") String restaurantName, Model uiModel) {
		
		// warmup stuff
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		Restaurant restaurant = restaurantService.fetchWarmedUp(restaurantName);
		uiModel.addAttribute("restaurant", restaurant);
		
		List<Order> allPreparedOrders = 
				orderService.findPreparedOrdersForRestaurant(restaurant);
		uiModel.addAttribute("allPreparedOrders", allPreparedOrders);

		List<Bill> allSubmittedBills = 
				billService.findSubmittedBillsForRestaurant(restaurant);
		uiModel.addAttribute("allSubmittedBills", allSubmittedBills);
		
		return "hartigehap/waiter";
	}

		
	@RequestMapping(value = "/bills/{billId}", method = RequestMethod.PUT)
	public String receiveBillEvent(
			@PathVariable("billId") String billId, 
			@RequestParam String event, 
			Model uiModel, Locale locale) {

		Bill bill = billService.findBillById(Long.valueOf(billId));
		Restaurant restaurant = warmupRestaurant(bill, uiModel);
		
		switch(event) {
		case "billHasBeenPaid":
			try {
				billService.billHasBeenPaid(bill);
			} catch (StateException e) {
				logger.error("Internal error has occurred! Order " + Long.valueOf(billId) 
						+ "has not been changed to served state!", e);
				// StateException triggers a rollback; consequently all Entities are invalidated by Hibernate
				// So new warmup needed
				warmupRestaurant(bill, uiModel);
				return "hartigehap/waiter";
			}
			break;
			
		default:
			logger.error("Internal error: event " + event + " not recognized");
			break;
		}
		
		return "redirect:/restaurants/" + restaurant.getId() + "/waiter";
	}

	private Restaurant warmupRestaurant(Bill bill, Model uiModel) {
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		Restaurant restaurant = restaurantService.fetchWarmedUp(bill.getDiningTable().getRestaurant().getId());
		uiModel.addAttribute("restaurant", restaurant);
		return restaurant;
	}
}
