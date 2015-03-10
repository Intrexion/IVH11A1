package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 
 * @author Erco
 */
@Entity
//optional
@Table(name = "BILLS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id") 
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"billState", "currentOrder", "orders"})
public class Bill extends DomainObject {
	private static final long serialVersionUID = 1L;

	@OneToOne(orphanRemoval=true, cascade=CascadeType.ALL)
	private BillState billState;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date paidTime;

	// unidirectional one-to-one relationship
	@OneToOne(cascade = CascadeType.ALL)
	private Order currentOrder;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "bill")
	private Collection<Order> orders = new ArrayList<Order>();
	
	// bidirectional one-to-many relationship
	@ManyToOne()
	private DiningTable diningTable;

	// bidirectional one-to-many relationship
	@ManyToOne(cascade = CascadeType.ALL)
	private Customer customer;
	
	public Bill() {
		billState = new BillStateCreated(this);
		currentOrder = new Order();
		currentOrder.setBill(this);
		orders.add(currentOrder);
	}


	/* business logic */

	@Transient
	public Collection<Order> getSubmittedOrders() {
		Collection<Order> submittedOrders = new ArrayList<Order>();
		Iterator<Order> orderIterator = orders.iterator();
		while (orderIterator.hasNext()) {
			Order tmp = orderIterator.next();
			if (tmp.isSubmittedOrSuccessiveState()) {
				submittedOrders.add(tmp);
			}
		}
		return submittedOrders;
	}

	/**
	 * price of *all* orders, so submitted orders and current (not
	 * yet submitted) order
	 * @return
	 */
	@Transient
	public int getPriceAllOrders() {
		int price = 0;
		Iterator<Order> orderIterator = orders.iterator();
		while (orderIterator.hasNext()) {
			price += orderIterator.next().getPrice();
		}
		return price;
	}

	/**
	 * price of the *submitted or successive state* orders only
	 * @return
	 */
	@Transient
	public int getPriceSubmittedOrSuccessiveStateOrders() {
		int price = 0;
		Iterator<Order> orderIterator = orders.iterator();
		while (orderIterator.hasNext()) {
			Order tmp = orderIterator.next();
			if (tmp.isSubmittedOrSuccessiveState()) {
				price += tmp.getPrice();
			}
		}
		return price;
	}

	public void submitOrder() throws StateException {
		currentOrder.submit();
		currentOrder = new Order();
		currentOrder.setBill(this);
		orders.add(currentOrder);
	}

	/**
	 * Verify it's possible to submit the bill, if not an exception is thrown.
	 * @throws StateException If the current order is not empty and in the created state
	 * @throws EmptyBillException If the bill is empty
	 */
	public void checkSubmit() throws StateException, EmptyBillException {
		boolean allEmpty = true;
		Iterator<Order> orderIterator = orders.iterator();
		while (orderIterator.hasNext()) {
			Order order = orderIterator.next();
			if (!order.isEmpty()) {
				allEmpty = false;
				break;
			}
		}

		if (allEmpty) {
			throw new EmptyBillException("not allowed to submit an empty bill");
		}

		if (!currentOrder.isEmpty() && currentOrder.getOrderStatus() == Order.OrderStatus.CREATED) {
			// the currentOrder is not empty, but not yet submitted
			throw new StateException("not allowed to submit an with currentOrder in created state");
		}
	}

	/*
	 * as the table gets a new bill, there is no risk that a customer keeps
	 * ordering on the submitted or paid bill
	 */
	public void submit() throws StateException, EmptyBillException {
		billState.submit();
	}

	@Transient
	public boolean isSubmitted() {
		return billState.isSubmitted();
	}

	public void paid() throws StateException {
		billState.paid();
	}

	public void setBillState(BillState billState) {
		this.billState = billState;
	}
}
