package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.avans.hartigehap.web.controller.rs.DateTimeToRSConverter;

/**
 * 
 * @author Erco
 */
@Entity
@Table(name = "CUSTOMERS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"firstName", "lastName", "bills"})
@NoArgsConstructor
public class Customer extends DomainObject {
	private static final long serialVersionUID = 1L;
	
	@NotEmpty(message = "{validation.firstname.NotEmpty.message}")
	private String firstName;

	@NotEmpty(message = "{validation.lastname.NotEmpty.message}")
	private String lastName;

	// works with hibernate 3.x
	// @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	// to allow using Joda's DateTime with hibernate 4.x use:
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	// needed to allow changing a date in the GUI
	@DateTimeFormat(iso = ISO.DATE)
	//Converter for restful
	@JsonSerialize(using = DateTimeToRSConverter.class)
	private DateTime birthDate;

	private int partySize;

	private String description;

	private String email;
	private String phone;
	
	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Column(name = "PHOTO")
	private byte[] photo;

	@OneToOne
	private Reservation reservation;
	
	// no cascading
	@ManyToMany
	@JsonBackReference
	private Collection<Restaurant> restaurants = new ArrayList<Restaurant>();

	// no cascading
	// bidirectional one-to-many; mapping on the database happens at the many side
	@OneToMany(mappedBy = "customer")
	@JsonBackReference
	private Collection<Bill> bills = new ArrayList<Bill>();
	
	private Customer(Builder build) {
		this.firstName = build.firstName;
		this.lastName = build.lastName;
		this.birthDate = build.birthDate;
		this.partySize = build.partySize;
		this.description = build.description;
		this.photo = build.photo;
		this.email = build.email;
		this.phone = build.phone;
	}

	// TODO	this method only updates user-editable fields
	// id, version, restaurants, bills are considered not user-editable
	public void updateEditableFields(Customer customer) {
        firstName = customer.firstName;
        lastName = customer.lastName;
        birthDate = customer.birthDate;
        description = customer.description;
        photo = customer.photo;
        partySize = customer.partySize;
	}

	// business logic
	public static class Builder{
		public final String firstName;
		public final String lastName;
		public int partySize;
		public DateTime birthDate;
		public String description;
		public byte[] photo;
		public String email;
		public String phone;
		
		public Builder(String firstName, String lastName){
			this.firstName = firstName;
			this.lastName = lastName;
		}
		
		public Builder setPartySize(int partySize) {
			this.partySize = partySize;
			return this;
		}

		public Builder setBirthDate(DateTime birthDate) {
			this.birthDate = birthDate;
			return this;
		}

		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder setPhoto(byte[] photo) {
			this.photo = photo.clone();
			return this;
		}

		public Builder setEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder setPhone(String phone) {
			this.phone = phone;
			return this;
		}
		
		public Customer build(){
			return new Customer(this);
		}
	}
}
