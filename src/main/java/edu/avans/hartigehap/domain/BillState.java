package edu.avans.hartigehap.domain;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;

import org.hibernate.annotations.DiscriminatorOptions;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Inheritance
@DiscriminatorColumn(name = "type")
@DiscriminatorOptions(force = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public abstract class BillState extends DomainObject {
	private static final long serialVersionUID = 1L;

	public abstract boolean isSubmitted();

	public abstract void paid(Bill bill) throws StateException;

	public abstract void submit(Bill bill) throws StateException, EmptyBillException;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		return this.getClass().isInstance(obj);
	}

	@Override
	public int hashCode() {
		return this.getClass().getName().hashCode();
	}
}
