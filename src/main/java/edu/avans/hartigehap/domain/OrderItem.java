package edu.avans.hartigehap.domain;

import javax.persistence.Entity;
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
@ToString(callSuper=true, includeFieldNames=true, of= {"quantity"})
@NoArgsConstructor
public abstract class OrderItem extends DomainObject {
	private static final long serialVersionUID = 1L;

	
	protected int quantity = 0;

	public OrderItem(int quantity) {
		this.quantity = quantity;
	}


	/* business logic */

	@Transient
	public abstract int getBaseQuantity();
	
	public abstract void incrementQuantity() ;

	public abstract void decrementQuantity();
	
	@Transient
	public abstract int getPrice();
	
	@Transient
	public abstract MenuItem getMenuItem();
	
	@Transient
	public abstract String getDescription();
}
