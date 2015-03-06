package edu.avans.hartigehap.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.Restaurant;

public interface DiningTableRepository extends PagingAndSortingRepository<DiningTable, Long> {
	
}