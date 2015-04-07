package edu.avans.hartigehap.domain;

import java.util.Collection;

public interface Criteria {
	public DiningTable meetCriteria(Reservation reservation, Collection<DiningTable> diningTables);
}
