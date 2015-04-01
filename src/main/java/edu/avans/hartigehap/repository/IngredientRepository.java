package edu.avans.hartigehap.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.avans.hartigehap.domain.FoodCategory;
import edu.avans.hartigehap.domain.Ingredient;

public interface IngredientRepository extends PagingAndSortingRepository<Ingredient, String> {
}
