package edu.avans.hartigehap.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.avans.hartigehap.domain.Ingredient;
import edu.avans.hartigehap.repository.IngredientRepository;
import edu.avans.hartigehap.service.IngredientService;

@Service("ingredientService")
@Repository
@Transactional
public class IngredientServiceImpl implements IngredientService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);
	
	@Autowired
	private IngredientRepository ingredientRepository;
	
	@Override
	public Ingredient findById(String name) {
		LOGGER.info("find reservation by id: " + name);
		return ingredientRepository.findOne(name);
	}
}
