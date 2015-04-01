package edu.avans.hartigehap.service.impl;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.repository.*;
import edu.avans.hartigehap.service.*;

import org.joda.time.DateTime;

@Service("restaurantPopulatorService")
@Repository
@Transactional
public class RestaurantPopulatorServiceImpl implements RestaurantPopulatorService {
	final Logger logger = LoggerFactory.getLogger(RestaurantPopulatorServiceImpl.class);
	
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private FoodCategoryRepository foodCategoryRepository;
	@Autowired
	private IngredientRepository ingredientRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private CustomerRepository customerRepository;
	
	private List<Meal> meals = new ArrayList<Meal>();
	private List<Ingredient> ingredients = new ArrayList<Ingredient>();
	private List<FoodCategory> foodCats = new ArrayList<FoodCategory>();
	private List<Drink> drinks = new ArrayList<Drink>();
	private List<Customer> customers = new ArrayList<Customer>();
	private List<Reservation> reservations = new ArrayList<Reservation>();
	private List<DiningTable> diningTables = new ArrayList<DiningTable>();

		
	/**
	 *  menu items, food categories and customers are common to all restaurants and should be created only once.
	 *  Although we can safely assume that the are related to at least one restaurant and therefore are saved via
	 *  the restaurant, we save them explicitly anyway
	 */
	private void createCommonEntities() {
		
		createFoodCategory("low fat");
		createFoodCategory("high energy");
		createFoodCategory("vegatarian");
		createFoodCategory("italian");
		createFoodCategory("asian");
		createFoodCategory("alcoholic drinks");
		createFoodCategory("energizing drinks");
		
		
		createIngredient("extra bell pepper", 1);
		createIngredient("extra salami", 2);
		createIngredient("extra mushrooms", 2);
		createIngredient("extra cheese", 1);
		
		createMeal("spaghetti", "spaghetti.jpg", 8, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1)}),
			Arrays.<Ingredient>asList(new Ingredient[]{ingredients.get(0), ingredients.get(2), ingredients.get(3)}));
		createMeal("macaroni", "macaroni.jpg", 8, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1)}),
			Arrays.<Ingredient>asList(new Ingredient[]{ingredients.get(0), ingredients.get(2), ingredients.get(3)}));	
		createMeal("canneloni", "canneloni.jpg", 9, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1)}),
			Arrays.<Ingredient>asList(new Ingredient[]{ingredients.get(0), ingredients.get(2), ingredients.get(3)}));
		createMeal("pizza", "pizza.jpg", 9, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1)}),
			Arrays.<Ingredient>asList(new Ingredient[]{ingredients.get(0),ingredients.get(1), ingredients.get(2), ingredients.get(3)}));
		createMeal("carpaccio", "carpaccio.jpg", 7, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(0)}),
			Arrays.<Ingredient>asList(new Ingredient[]{ingredients.get(3)}));
		createMeal("ravioli", "ravioli.jpg", 8, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1), foodCats.get(2)}),
			Arrays.<Ingredient>asList(new Ingredient[]{ingredients.get(2), ingredients.get(3)}));

		createDrink("beer", "beer.jpg", 1, Drink.Size.LARGE,
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(5)}));
		createDrink("coffee", "coffee.jpg", 1, Drink.Size.MEDIUM,
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(6)}));
		
		byte[] photo = new byte[]{127,-128,0};
		createCustomer("piet", "bakker", new DateTime(), 1, "description", photo, "a@a.nl", "123123");
		createCustomer("piet", "bakker", new DateTime(), 1, "description", photo, "a@a.nl", "123123");
		createCustomer("piet", "bakker", new DateTime(), 1, "description", photo, "a@a.nl", "123123");		
	}

	private void createFoodCategory(String tag) {
		FoodCategory foodCategory = new FoodCategory(tag);
		foodCategory = foodCategoryRepository.save(foodCategory);
		foodCats.add(foodCategory);
	}
	
	private void createIngredient(String name, int price) {
		Ingredient ingredient = new Ingredient(name, price);
		ingredient = ingredientRepository.save(ingredient);
		ingredients.add(ingredient);
	}
	
	private void createMeal(String name, String image, int price, String recipe, List<FoodCategory> foodCats, List<Ingredient> possibleAdditions) {
		Meal meal = new Meal(name, image, price, recipe);
		// as there is no cascading between FoodCategory and MenuItem (both ways), it is important to first 
		// save foodCategory and menuItem before relating them to each other, otherwise you get errors
		// like "object references an unsaved transient instance - save the transient instance before flushing:"
		meal.addFoodCategories(foodCats);
		meal.addPossibleAdditions(possibleAdditions);
		meal = menuItemRepository.save(meal);
		meals.add(meal);
	}
	
	private void createDrink(String name, String image, int price, Drink.Size size, List<FoodCategory> foodCats) {
		Drink drink = new Drink(name, image, price, size);
		drink = menuItemRepository.save(drink);
		drink.addFoodCategories(foodCats);
		drink.addPossibleAdditions(new ArrayList<Ingredient>());
		drinks.add(drink);
	}
	
	private void createCustomer(String firstName, String lastName, DateTime birthDate,
		int partySize, String description, byte[] photo, String email, String phone) {
		//Customer customer2 = new Customer(firstName, lastName, birthDate, partySize, description, photo); 
		Customer customer = new Customer.Builder(firstName, lastName)
										.setBirthDate(birthDate)
										.setDescription(description)
										.setPartySize(partySize)
										.setPhoto(photo)
										.setEmail(email)
										.setPhone(phone)
										.build();
		customers.add(customer);
		customerRepository.save(customer);
	}
	
	private void createDiningTables(int numberOfTables, Restaurant restaurant) {
		for(int i=0; i<numberOfTables; i++) {
			DiningTable diningTable = new DiningTable(i+1, i+2);
			diningTable.setRestaurant(restaurant);
			restaurant.getDiningTables().add(diningTable);
			diningTables.add(diningTable);
		}
	}
	
	private Restaurant populateRestaurant(Restaurant restaurant) {
				
		// will save everything that is reachable by cascading
		// even if it is linked to the restaurant after the save
		// operation
		restaurant = restaurantRepository.save(restaurant);

		// every restaurant has its own dining tables
		createDiningTables(5, restaurant);

		// for the moment every restaurant has all available food categories 
		for(FoodCategory foodCat : foodCats) {
			restaurant.getMenu().getFoodCategories().add(foodCat);
		}

		// for the moment every restaurant has the same menu 
		for(Meal meal : meals) {
			restaurant.getMenu().getMeals().add(meal);
		}

		// for the moment every restaurant has the same menu 
		for(Drink drink : drinks) {
			restaurant.getMenu().getDrinks().add(drink);
		}
		
		// for the moment, every customer has dined in every restaurant
		// no cascading between customer and restaurant; therefore both restaurant and customer
		// must have been saved before linking them one to another
		for(Customer customer : customers) {
			customer.getRestaurants().add(restaurant);
			restaurant.getCustomers().add(customer);
		}
		return restaurant;
	}

	private void createReservation(DateTime start, DateTime end, String description, Restaurant rest, DiningTable table, Customer cust){
		Reservation reservation = new Reservation(start, end, description);
		reservation = reservationRepository.save(reservation);
		reservations.add(reservation);
		cust.setReservation(reservation);
		reservation.setCustomer(cust);
		reservation.setRestaurant(rest);
		rest.getReservations().add(reservation);
		reservation.setDiningTable(table);
		table.getReservations().add(reservation);
	}
	
	public void createRestaurantsWithInventory() {
		
		createCommonEntities();

		Restaurant restaurant = new Restaurant(HARTIGEHAP_RESTAURANT_NAME, "deHartigeHap.jpg");
		restaurant = populateRestaurant(restaurant);
		
		Restaurant restaurant1 = new Restaurant(PITTIGEPANNEKOEK_RESTAURANT_NAME, "dePittigePannekoek.jpg");
		restaurant1 = populateRestaurant(restaurant1);
		
		Restaurant restaurant2 = new Restaurant(HMMMBURGER_RESTAURANT_NAME, "deHmmmBurger.jpg");
		restaurant2 = populateRestaurant(restaurant2);
		DiningTable diningTable = null;
		for(DiningTable d : diningTables){
			if(d.getRestaurant().getId().equals(restaurant.getId())){
				diningTable = d;
				break;
			}
		}
		createReservation(new DateTime(), new DateTime().plusHours(2), "", restaurant, diningTable, customers.get(0));
	}	
}
