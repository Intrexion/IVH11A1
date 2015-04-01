package edu.avans.hartigehap.model;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderItemModel {

	private String menuItemName;
	
	private HashMap<String, Integer> additions = new HashMap<>();	
	
}