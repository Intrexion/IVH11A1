package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.CascadeType;

import org.joda.time.DateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 
 * @author Erco
 */
@Entity
@Table(name = "DININGTABLES")
// optional
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"bills", "currentBill"})
public class DiningTable extends DomainObject {
	private static final long serialVersionUID = 1L;

	private int tableNr;
	private int seats;
	// example of an *unidirectional* one-to-one relationship, mapped on
	// database by diningTable side
	@OneToOne(cascade = CascadeType.ALL)
	@JsonBackReference
	private Bill currentBill;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "diningTable")
	@JsonBackReference
	private Collection<Bill> bills = new ArrayList<Bill>();

	@OneToMany(mappedBy = "diningTable", cascade = CascadeType.ALL)
	@JsonBackReference
	private Collection<Reservation> reservations = new ArrayList<Reservation>();
	
	@ManyToOne()
	@JsonBackReference
	private Restaurant restaurant;

	public DiningTable() {
		// when the system resets, the c'tor is executed and a new Bill object
		// is created (which in its turn creates a new order object. However, 
		// when the dining table becomes managed, the currentBill as was 
		// stored in the database is retrieved, and the new Bill and new Order
		// object, which were not managed yet are discarded.
		currentBill = new Bill();
		currentBill.setDiningTable(this);
		bills.add(currentBill);
	}
	
	public Collection<Reservation> getReservationsByDate(DateTime date){
		
		Collection<Reservation> newReservations = reservations.stream().filter(r -> r.getStartDate().getDayOfYear() == date.getDayOfYear()).collect(Collectors.toList());
		
		return newReservations;
		}

	public DiningTable(int tableNr, int seats) {
		this.tableNr = tableNr;
		this.seats = seats;
		// when the system resets, the c'tor is executed and a new Bill object
		// is created (which in its turn creates a new order object. However, 
		// when the dining table becomes managed, the currentBill as was 
		// stored in the database is retrieved, and the new Bill and new Order
		// object, which were not managed yet are discarded.
		currentBill = new Bill();
		currentBill.setDiningTable(this);
		bills.add(currentBill);
	}

	/* business logic */

	public void warmup() {
		Iterator<OrderItem> orderItemIterator = currentBill.getCurrentOrder()
				.getOrderItems().iterator();
		while (orderItemIterator.hasNext()) {
			orderItemIterator.next().getId();
			// note: menu items have been warmed up via the restaurant->menu
			// relation; therefore it
			// is not needed to warm these objects via this relation
		}
	}

	public void submitBill() throws StateException, EmptyBillException {
		currentBill.submit();
		currentBill = new Bill();
		currentBill.setDiningTable(this);
		bills.add(currentBill);
	}

}
