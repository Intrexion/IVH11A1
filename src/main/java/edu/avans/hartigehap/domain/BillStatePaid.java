package edu.avans.hartigehap.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue(BillStatePaid.DISCRIMINATOR)
@NoArgsConstructor
public class BillStatePaid extends BillState {
	private static final long serialVersionUID = 1L;

	public static final String DISCRIMINATOR = "paid";

	public BillStatePaid(Bill bill) {
		super(bill);
	}

	@Override
	public boolean isSubmitted() {
		return false;
	}

	@Override
	public void paid() throws StateException {
		throw new StateException("not allowed to pay an bill that is not in the submitted state");
	}

	@Override
	public void submit() throws StateException, EmptyBillException {
		throw new StateException("not allowed to submit an already submitted bill");
	}
}
