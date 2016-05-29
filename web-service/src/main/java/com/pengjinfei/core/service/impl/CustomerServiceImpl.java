package com.pengjinfei.core.service.impl;

import com.pengjinfei.common.lock.Lock;
import com.pengjinfei.core.dao.CustomerDao;
import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private CustomerDao customerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    @Lock("testInsert")
    public void insertCustomer(Customer customer) {
        customerDao.insert(customer);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerInsertCustomer(Customer customer) {
        customerDao.insert(customer);
    }

    public Customer getById(String id) {
        return customerDao.getById(id);
    }

    @Transactional
    public void updateByName(String name) {
        customerDao.updateByName(name);
    }


}
