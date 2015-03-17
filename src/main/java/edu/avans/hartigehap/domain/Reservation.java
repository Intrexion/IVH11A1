package edu.avans.hartigehap.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "RESERVATIONS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@NoArgsConstructor
public class Reservation extends DomainObject {
	private static final long serialVersionUID = 1L;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	// needed to allow changing a date in the GUI
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private DateTime startDate;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	// needed to allow changing a date in the GUI
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private DateTime endDate;

	private String description;

	@OneToOne(mappedBy="reservation")
	private Customer customer;

	@ManyToOne
	private Restaurant restaurant;

	@ManyToOne
	private DiningTable diningTable;
	
	public void updateEditableFields(Reservation reservation) {
        customer.updateEditableFields(reservation.getCustomer());
        
	}
	
	public Reservation(DateTime startDate, DateTime endDate, String description){
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
	}

}
