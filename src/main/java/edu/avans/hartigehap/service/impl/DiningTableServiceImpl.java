package edu.avans.hartigehap.service.impl;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.repository.*;
import edu.avans.hartigehap.service.DiningTableService;

import com.google.common.collect.Lists;


@Service("diningTableService")
@Repository
@Transactional(rollbackFor = StateException.class)
public class DiningTableServiceImpl implements DiningTableService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DiningTableServiceImpl.class);
	
	@Autowired
	private DiningTableRepository diningTableRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;
	
	@Transactional(readOnly=true)
	public List<DiningTable> findAll() {
		LOGGER.info("Find all diningtables");
		return Lists.newArrayList(diningTableRepository.findAll());
	}

	@Transactional(readOnly=true)
	public DiningTable findById(Long id) {
		LOGGER.info("Find diningtable by id: " + id);
		return diningTableRepository.findOne(id);
	}
	
	public DiningTable save(DiningTable diningTable) {
		LOGGER.info("Find by save diningTable" + diningTable.getId());
		return diningTableRepository.save(diningTable);
	}

	public void delete(Long id) {
		LOGGER.info("delete diningtable: " + id);
		diningTableRepository.delete(id);
	}

	@Transactional(readOnly=true)
	public Page<DiningTable> findAllByPage(Pageable pageable) {
		LOGGER.info("find all diningtables by page");
		return diningTableRepository.findAll(pageable);
	}

	// to be able to follow associations outside the context of a transaction,
	// prefetch the associated entities by traversing the associations
	@Transactional(readOnly=true)
	public DiningTable fetchWarmedUp(Long id) {
		LOGGER.info("(fetchWarmedUp) diningTable id: " + id);

		// finding an item using find
		DiningTable diningTable = diningTableRepository.findOne(id);
		
		// the following code will deliberately cause a null pointer exception, if something is wrong
		LOGGER.info("diningTable = " + diningTable.getId());
		
		diningTable.warmup();
				
		return diningTable;
	}

	public void addOrderItem(DiningTable diningTable, String menuItemName, Map<Ingredient, Integer> additions) {
		LOGGER.info("Add orderitem");
		MenuItem menuItem = menuItemRepository.findOne(menuItemName);
		diningTable.getCurrentBill().getCurrentOrder().addOrderItem(menuItem, additions);
	}
	
	public void deleteOrderItem(DiningTable diningTable, String orderItemId) {
		LOGGER.info("delete orderitem. orderItemId" + orderItemId);
		diningTable.getCurrentBill().getCurrentOrder().deleteOrderItem(orderItemId);
	}
	
	public void submitOrder(DiningTable diningTable)
		throws StateException {
		LOGGER.info("submitOrder");
		diningTable.getCurrentBill().submitOrder();
		
		// for test purposes: to cause a rollback, throw new StateException("boe")
	}
	
	public void submitBill(DiningTable diningTable)
		throws StateException, EmptyBillException {
		LOGGER.info("submit bill");
		diningTable.submitBill();
	}
}
