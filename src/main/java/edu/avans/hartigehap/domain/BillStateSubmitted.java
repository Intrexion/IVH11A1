package edu.avans.hartigehap.domain;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue(BillStateSubmitted.DISCRIMINATOR)
@NoArgsConstructor
public class BillStateSubmitted extends BillState {
	private static final long serialVersionUID = 1L;

	public static final String DISCRIMINATOR = "submitted";

	public BillStateSubmitted(Bill bill) {
		super(bill);
	}

	@Override
	public boolean isSubmitted() {
		return true;
	}

	@Override
	public void paid() throws StateException {
		bill.setBillState(new BillStatePaid(bill));
		bill.setPaidTime(new Date());
	}

	@Override
	public void submit() throws StateException, EmptyBillException {
		throw new StateException("not allowed to submit an already submitted bill");
	}
}
