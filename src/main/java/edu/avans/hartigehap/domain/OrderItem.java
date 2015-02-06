package edu.avans.hartigehap.domain;

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

/**
 * 
 * @author Erco
 */
@Entity
@Table(name = "ORDERITEMS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"menuItem", "quantity"})
@NoArgsConstructor
public class OrderItem extends DomainObject {
	private static final long serialVersionUID = 1L;

	// unidirectional one-to-one
	// deliberate: no cascade!!
	@OneToOne()
	private MenuItem menuItem;
	
	private int quantity = 0;

	public OrderItem(MenuItem menuItem, int quantity) {
		this.menuItem = menuItem;
		this.quantity = quantity;
	}


	/* business logic */

	public void incrementQuantity() {
		this.quantity++;
	}

	public void decrementQuantity() {
		assert quantity > 0 : "quantity cannot be below 0";
		this.quantity--;
	}

	@Transient
	public int getPrice() {
		return menuItem.getPrice() * quantity;
	}
}
