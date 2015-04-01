package edu.avans.hartigehap.domain;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
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
@Table(name = "ORDERITEMS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"menuItem", "quantity"})
@NoArgsConstructor
public class BasicOrderItem extends OrderItem{
	private static final long serialVersionUID = 1L;
	// unidirectional one-to-one
	// deliberate: no cascade!!
	@OneToOne()
	protected MenuItem menuItem;
	

	public BasicOrderItem(MenuItem menuItem) {
		this.menuItem = menuItem;
		quantity = 1;
	}

	
	@Override
	@Transient
	public int getPrice() {
		return menuItem.getPrice() * quantity;
	}

	@Override
	public void incrementQuantity() {
		quantity ++;
		
	}

	@Override
	public void decrementQuantity() {
		assert quantity > 0 : "quantity cannot be below 0";
		this.quantity--;
	}
	
	@Override
	@Transient
	public String getDescription(){
		return quantity + "x " + menuItem.getId();
	}
	
}
