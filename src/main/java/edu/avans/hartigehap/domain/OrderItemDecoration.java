package edu.avans.hartigehap.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"ingredient"})
@NoArgsConstructor
public class OrderItemDecoration extends OrderItem{
	private static final long serialVersionUID = 1L;

	@OneToOne(cascade = CascadeType.ALL)
	private OrderItem orderItem;
	
	@OneToOne
	private Ingredient ingredient;
	
	public OrderItemDecoration(OrderItem orderItem, Ingredient ingredient, int quantity){
		super(quantity);
		this.ingredient = ingredient;
		this.orderItem = orderItem;
	}

	@Override
	@Transient
	public int getPrice() {		
		return orderItem.getPrice() + (ingredient.getPrice() * quantity * getBaseQuantity());
	}
	
	@Transient
	public MenuItem getMenuItem(){
		return orderItem.getMenuItem();
	}

	@Override
	public void incrementQuantity() {
		orderItem.incrementQuantity();
	}

	@Override
	public void decrementQuantity() {
		orderItem.decrementQuantity();
	}

	@Override
	public String getDescription() {
		return orderItem.getDescription() + " + " + quantity + "x " + ingredient.getId();
	}

	@Override
	public int getBaseQuantity() {
		return orderItem.getBaseQuantity();
	}

}
