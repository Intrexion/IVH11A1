package edu.avans.hartigehap.domain;

import javax.persistence.CascadeType;
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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.avans.hartigehap.web.controller.rs.DateTimeToRSConverter;
import edu.avans.hartigehap.web.controller.DateAndTime;
import edu.avans.hartigehap.web.controller.DateTimeAdapter;
import edu.avans.hartigehap.web.controller.DateTimeProvider;

@Entity
@Table(name = "RESERVATIONS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@NoArgsConstructor
public class Reservation extends DomainObject {
	private static final long serialVersionUID = 1L;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	// needed to allow changing a date in the GUI
	@DateTimeFormat(iso = ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm")
	@JsonSerialize(using = DateTimeToRSConverter.class)
	private DateTime startDate;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	// needed to allow changing a date in the GUI
	@DateTimeFormat(iso = ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm")
	@JsonSerialize(using = DateTimeToRSConverter.class)
	private DateTime endDate;

	private String startTime, endTime,day;
	
	private String description;

	@OneToOne(mappedBy="reservation", cascade = CascadeType.ALL)
	private Customer customer;

	@ManyToOne
	private Restaurant restaurant;

	@ManyToOne
	private DiningTable diningTable;
	
	public void updateEditableFields(Reservation reservation) {
		
		
		customer.setPartySize(reservation.getCustomer().getPartySize());
        diningTable = reservation.getDiningTable();
        description = reservation.getDescription();
    	DateTimeProvider provider = new DateTimeAdapter(new DateAndTime(reservation.getDay(), reservation.getStartTime()));
		startDate = provider.getDateTime();
    	provider = new DateTimeAdapter(new DateAndTime(reservation.getDay(), reservation.getEndTime()));
		endDate = provider.getDateTime();
        startTime = reservation.getStartTime();
        endTime = reservation.getEndTime();
        day= reservation.getDay();
        
	}
	
	public Reservation(DateTime startDate, DateTime endDate, String description){
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		day = startDate.toString("yyyy-MM-dd");
		startTime = startDate.toString("HH:mm");
		endTime = endDate.toString("HH:mm");
	}
}
