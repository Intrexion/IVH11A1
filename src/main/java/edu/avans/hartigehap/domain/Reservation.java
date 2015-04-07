package edu.avans.hartigehap.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.avans.hartigehap.web.controller.rs.DateTimeToJsonAdapter;

@Entity
@Table(name = "RESERVATIONS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@NoArgsConstructor
public class Reservation extends DomainObject {
	private static final long serialVersionUID = 1L;
//	------------------------------------------------------------------------------------
//	DENNIS BLIJF HIER VANAF, TOM HEEFT HET NOG NODIG!
//	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
//	// needed to allow changing a date in the GUI
//	@DateTimeFormat(iso = ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm")
//	//Alleen zodat json dit veld negeert en de getter naar String pakt
//	@JsonIgnore
//	/* Dit is de mooie manier om het op te lossen, klasse heb ik hiervoor expres er in gelaten. Deze manier staat in de customer als voorbeeld
//	 * @JsonSerialize(using = DateTimeToRSConverter.class)
//	 */
//	private DateTime startDate;
//
//	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
//	// needed to allow changing a date in the GUI
//	@DateTimeFormat(iso = ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm")
//	//Alleen zodat json dit veld negeert en de getter naar String pakt
//	@JsonIgnore
//	/* Dit is de mooie manier om het op te lossen, klasse heb ik hiervoor expres er in gelaten. Deze manier staat in de customer als voorbeeld
//	 * @JsonSerialize(using = DateTimeToRSConverter.class)
//	 */
//	private DateTime endDate;

	@JsonIgnore
	@DateTimeFormat(iso = ISO.TIME, pattern = "HH:mm")
	private LocalTime startTime, endTime;
	@DateTimeFormat(iso = ISO.DATE_TIME, pattern = "yyyy-MM-dd")
	@JsonIgnore
	private LocalDate day;
	
	private String description;
	
	private String code;

	@OneToOne(mappedBy="reservation", cascade = CascadeType.ALL)
	private Customer customer;

	@ManyToOne
	private Restaurant restaurant;

	@ManyToOne
	private DiningTable diningTable;
	
	public Reservation(LocalTime startTime, LocalTime endTime, LocalDate day, String description){
		this.startTime = startTime;
		this.endTime = endTime;
		this.day = day;
		this.description = description;
	}
	
	public void updateEditableFields(Reservation reservation) {customer.setPartySize(reservation.getCustomer().getPartySize());
        diningTable = reservation.getDiningTable();
        description = reservation.getDescription();
        startTime = reservation.getStartTime();
        endTime = reservation.getEndTime();
        day= reservation.getDay();   
	}
	
	@Transient
	public String getEndDateString(){
		DateTimeToJsonAdapter adapter = new DateTimeToJsonAdapter(endTime, day);
		return adapter.getJson();
	}
	
	@Transient
	public String getStartDateString(){
		DateTimeToJsonAdapter adapter = new DateTimeToJsonAdapter(startTime, day);
		return adapter.getJson();
	}
	
	@Transient
	@JsonIgnore
	public DateTime getStartDate(){
		return new DateTime(day.getYear(), day.getMonthOfYear(), day.getDayOfMonth(), startTime.getHourOfDay(), startTime.getMinuteOfHour());
	}
	
	@Transient
	@JsonIgnore
	public DateTime getEndDate(){
		
		return new DateTime(day.getYear(), day.getMonthOfYear(), day.getDayOfMonth(), endTime.getHourOfDay(), endTime.getMinuteOfHour());
	}
}
