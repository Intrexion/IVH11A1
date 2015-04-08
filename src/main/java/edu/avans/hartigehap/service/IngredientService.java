package edu.avans.hartigehap.service;

import edu.avans.hartigehap.domain.Ingredient;

public interface IngredientService {
	Ingredient findById(String name);
}
