package edu.avans.hartigehap.domain;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(BillStateSubmitted.DISCRIMINATOR)
public class BillStateSubmitted extends BillState {
	private static final long serialVersionUID = 1L;

	public static final String DISCRIMINATOR = "submitted";

	@Override
	public boolean isSubmitted() {
		return true;
	}

	@Override
	public void paid(Bill bill) throws StateException {
		bill.setBillState(new BillStatePaid());
		bill.setPaidTime(new Date());
	}

	@Override
	public void submit(Bill bill) throws StateException, EmptyBillException {
		throw new StateException("not allowed to submit an already submitted bill");
	}
}
