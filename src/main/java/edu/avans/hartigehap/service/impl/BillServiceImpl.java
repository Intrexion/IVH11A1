package edu.avans.hartigehap.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.avans.hartigehap.repository.*;
import edu.avans.hartigehap.service.*;
import edu.avans.hartigehap.aop.MyExecutionTime;
import edu.avans.hartigehap.domain.*;

@Service("billService")
@Repository
@Transactional(rollbackFor = StateException.class)
public class BillServiceImpl implements BillService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BillServiceImpl.class);

	@Autowired
	private BillRepository billRepository;
	
	@MyExecutionTime
	@Transactional(readOnly=true)
	public Bill findById(Long billId) {
		LOGGER.info("find bill by id: " + billId);
		return billRepository.findOne(billId);
	}

	
	public void billHasBeenPaid(Bill bill) throws StateException {
		LOGGER.info("Bill has been paid");
		bill.paid();
	}

	@MyExecutionTime
	@Transactional(readOnly=true)
	public List<Bill> findSubmittedBillsForRestaurant(Restaurant restaurant) {
		LOGGER.info("Find submitted bills by restaurant");
		// a query created using a repository method name
		List<Bill> submittedBillsList = billRepository.findByBillState_StatusTypeAndDiningTable_Restaurant(
				BillStateSubmitted.DISCRIMINATOR,
				restaurant,
				new Sort(Sort.Direction.ASC, "submittedTime"));

		return submittedBillsList;
	}	
}
