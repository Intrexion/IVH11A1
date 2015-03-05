package edu.avans.hartigehap.domain;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("created")
public class BillStateCreated extends BillState {
	private static final long serialVersionUID = 1L;

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
		// Verify the bill can be submitted
		bill.checkSubmit();

		bill.setBillState(new BillStateSubmitted());
		bill.setSubmittedTime(new Date());
	}
}
