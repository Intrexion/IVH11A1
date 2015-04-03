package edu.avans.hartigehap.web.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.model.OrderItemModel;
import edu.avans.hartigehap.repository.IngredientRepository;
import edu.avans.hartigehap.service.*;
import edu.avans.hartigehap.web.form.Message;

import org.springframework.web.servlet.mvc.support.*;

@Controller
public class DiningTableController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiningTableController.class);

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private DiningTableService diningTableService;
	@Autowired
	private IngredientRepository ingredientRepository;

	@RequestMapping(value = "/diningTables/{diningTableId}", method = RequestMethod.GET)
	public String showTable(@PathVariable("diningTableId") String diningTableId, Model uiModel) {
		LOGGER.info("diningTable = " + diningTableId);

		warmupRestaurant(diningTableId, uiModel);
		OrderItemModel model = new OrderItemModel();
		uiModel.addAttribute("orderItemModel", model);

		return "hartigehap/diningtable";
	}
	
	@RequestMapping(value = "/diningTables/{diningTableId}/menuItems", method = RequestMethod.POST)
	public String addMenuItem(
			@PathVariable("diningTableId") String diningTableId,
			OrderItemModel model, Model uiModel) {
		Map<Ingredient, Integer> additions = new HashMap<>();
		for(String s : model.getAdditions().keySet()){
			additions.put(ingredientRepository.findOne(s), model.getAdditions().get(s));			
		}
		
		for(Ingredient i : additions.keySet()){
			System.out.println(i.getId());			
			System.out.println(additions.get(i));			
			System.out.println("----------");				
		}
		
		
		DiningTable diningTable = diningTableService.fetchWarmedUp(Long.valueOf(diningTableId));
		uiModel.addAttribute("diningTable", diningTable);

		diningTableService.addOrderItem(diningTable, model.getMenuItemName(), additions);
		
		return "redirect:/diningTables/" + diningTableId;
	}
	
	@RequestMapping(value = "/diningTables/{diningTableId}/menuItems/{orderItemId}", method = RequestMethod.DELETE)
	public String deleteMenuItem(
			@PathVariable("diningTableId") String diningTableId,
			@PathVariable("orderItemId") String orderItemId, OrderItemModel model,
			Model uiModel) {
		
		Map<Ingredient, Integer> additions = new HashMap<>();
		for(String s : model.getAdditions().keySet()){
			additions.put(ingredientRepository.findOne(s), model.getAdditions().get(s));			
		}
		
		for(Ingredient i : additions.keySet()){
			System.out.println(i.getId());			
			System.out.println(additions.get(i));			
			System.out.println("----------");			
		}

		DiningTable diningTable = diningTableService.fetchWarmedUp(Long.valueOf(diningTableId));
		uiModel.addAttribute("diningTable", diningTable);
		
		diningTableService.deleteOrderItem(diningTable, orderItemId);

		return "redirect:/diningTables/" + diningTableId;
	}

	@RequestMapping(value = "/diningTables/{diningTableId}", method = RequestMethod.PUT)
	public String receiveEvent(
			@PathVariable("diningTableId") String diningTableId, 
			@RequestParam String event, 
			RedirectAttributes redirectAttributes,
			Model uiModel, Locale locale) { 
		
		LOGGER.info("(receiveEvent) diningTable = " + diningTableId);

		
		// because of REST, the "event" parameter is part of the body. It therefore cannot be used for
		// the request mapping so all events for the same resource will be handled by the same
		// controller method; so we end up with an if statement
		
		switch(event) {
		case "submitOrder":
			return submitOrder(diningTableId, redirectAttributes, uiModel, locale);
			// break unreachable

		case "submitBill":	
			return submitBill(diningTableId, redirectAttributes, uiModel, locale);
			// break unreachable
			
		default:	
			warmupRestaurant(diningTableId, uiModel);
			LOGGER.error("internal error: event " + event + "not recognized");
			return "hartigehap/diningtable";			
		}
	}
	
	private String submitOrder(String diningTableId, RedirectAttributes redirectAttributes, Model uiModel, Locale locale) {
		DiningTable diningTable = warmupRestaurant(diningTableId, uiModel);
		try {
			diningTableService.submitOrder(diningTable);
		} catch (StateException e) {
			LOGGER.error("StateException", e);
			uiModel.addAttribute("message", new Message("error",
					messageSource.getMessage("message_submit_order_fail", new Object[]{}, locale)));

			// StateException triggers a rollback; consequently all Entities are invalidated by Hibernate
			// So new warmup needed
			diningTable = warmupRestaurant(diningTableId, uiModel);

			return "hartigehap/diningtable";
		}
		// store the message temporarily in the session to allow displaying after redirect
		redirectAttributes.addFlashAttribute("message", new Message("success", 
				messageSource.getMessage("message_submit_order_success", new Object[]{}, locale))); 
		return "redirect:/diningTables/" + diningTableId;
		
	}
		
	private String submitBill(String diningTableId, RedirectAttributes redirectAttributes, Model uiModel, Locale locale) {
		DiningTable diningTable = warmupRestaurant(diningTableId, uiModel);
		try {
			diningTableService.submitBill(diningTable);
		} catch(EmptyBillException e) {
			LOGGER.error("EmptyBillException", e);
			uiModel.addAttribute("message", new Message("error", messageSource.getMessage("message_submit_empty_bill_fail", new Object[]{}, locale))); 
			return "hartigehap/diningtable";
		} catch(StateException e) {
			LOGGER.error("StateException", e);
			uiModel.addAttribute("message", new Message("error", messageSource.getMessage("message_submit_bill_fail", new Object[]{}, locale))); 

			// StateException triggers a rollback; consequently all Entities are invalidated by Hibernate
			// So new warmup needed
			diningTable = warmupRestaurant(diningTableId, uiModel);

			return "hartigehap/diningtable";
		}
		// store the message temporarily in the session to allow displaying after redirect
		redirectAttributes.addFlashAttribute("message", new Message("success",
				messageSource.getMessage("message_submit_bill_success", new Object[]{}, locale))); 
		return "redirect:/diningTables/" + diningTableId;
	}

		private DiningTable warmupRestaurant(String diningTableId, Model uiModel) {
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		DiningTable diningTable = diningTableService.fetchWarmedUp(Long.valueOf(diningTableId));
		uiModel.addAttribute("diningTable", diningTable);		
		Restaurant restaurant = restaurantService.fetchWarmedUp(diningTable.getRestaurant().getId());
		uiModel.addAttribute("restaurant", restaurant);
		
		return diningTable;
	}
}
