package edu.avans.hartigehap.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.DiscriminatorOptions;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Inheritance
@DiscriminatorColumn(name = "type")
@DiscriminatorOptions(force = true)
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public abstract class BillState extends DomainObject {
	private static final long serialVersionUID = 1L;

	// Map the discriminator value as a read-only property
	@Column(name="type", nullable=false, updatable=false, insertable=false)
	private String statusType;

	@Getter
	@Setter
	@OneToOne(mappedBy="billState")
	protected Bill bill;

	public BillState(Bill bill) {
		this.bill = bill;
	}

	public abstract boolean isSubmitted();

	public abstract void paid() throws StateException;

	public abstract void submit() throws StateException, EmptyBillException;

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
