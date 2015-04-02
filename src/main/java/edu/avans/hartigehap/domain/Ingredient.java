package edu.avans.hartigehap.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

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
@Table(name = "INGREDIENTS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {})
@NoArgsConstructor
public class Ingredient extends DomainObjectNaturalId {
	private static final long serialVersionUID = 1L;
		
//	private String name;
	// JPA is case sensitive: the corresponding column name will be in small
	// caps "price"
	private int price;
	
	public Ingredient(String name, int price) {
		super(name);
		this.price = price;

	}
	/* business logic */
}
