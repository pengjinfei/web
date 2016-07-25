package com.pengjinfei.core.service.impl;

import com.pengjinfei.core.dao.CustomerDao;
import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
@Service("otherCustomerService")
public class CustomerServiceOtherImpl implements CustomerService {

    @Autowired
    CustomerDao customerDao;

    public void insertCustomer(Customer customer) {

    }

    public void innerInsertCustomer(Customer customer) {

    }

    public Customer getById(String id) {
        Customer customer = customerDao.getById(id);
        customer.setName(customer.getName() + "other");
        return customer;

    }

    public void updateByName(String name) {

    }

    @Override
    public void insertForTestQuartz() {

    }
}
