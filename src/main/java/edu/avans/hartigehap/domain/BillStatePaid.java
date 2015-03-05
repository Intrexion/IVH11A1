package edu.avans.hartigehap.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(BillStatePaid.DISCRIMINATOR)
public class BillStatePaid extends BillState {
	private static final long serialVersionUID = 1L;

	public static final String DISCRIMINATOR = "paid";

	@Override
	public boolean isSubmitted() {
		return false;
	}

	@Override
	public void paid(Bill bill) throws StateException {
		throw new StateException("not allowed to pay an bill that is not in the submitted state");
	}

	@Override
	public void submit(Bill bill) throws StateException, EmptyBillException {
		throw new StateException("not allowed to submit an already submitted bill");
	}
}
