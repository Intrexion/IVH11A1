package edu.avans.hartigehap.domain;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue(BillStateCreated.DISCRIMINATOR)
@NoArgsConstructor
public class BillStateCreated extends BillState {
	private static final long serialVersionUID = 1L;

	public static final String DISCRIMINATOR = "created";

	public BillStateCreated(Bill bill) {
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
		// Verify the bill can be submitted
		bill.checkSubmit();

		bill.setBillState(new BillStateSubmitted(bill));
		bill.setSubmittedTime(new Date());
	}
}
