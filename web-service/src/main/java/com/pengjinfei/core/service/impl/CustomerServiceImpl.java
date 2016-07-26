package com.pengjinfei.core.service.impl;

import com.pengjinfei.common.quartz.TimerJob;
import com.pengjinfei.core.dao.CustomerDao;
import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

    private static Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerDao customerDao;

    @Autowired
    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
//    @Lock("testInsert")
    public void insertCustomer(Customer customer) {
        customerDao.insert(customer);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerInsertCustomer(Customer customer) {
        customerDao.insert(customer);
    }

    @Cacheable(value = "custom", key = "#id")
    public Customer getById(String id) {
        logger.info("Can not find custom of id=" + id + ", go to database.");
        return customerDao.getById(id);
    }

    @Transactional
    public void updateByName(String name) {
        customerDao.updateByName(name);
    }

    @Override
    @TimerJob(name="测试quartz",cronExpression = "0/5 * * * * ?",maxConcurrent = 1)
    public void insertForTestQuartz() {
        logger.info("PENGJINFEI COMMING!");
    }

}
