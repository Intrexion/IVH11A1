package edu.avans.hartigehap.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderItemModel {

	private String menuItemName;
	
	private Map<String, Integer> additions = new HashMap<>();
	
}