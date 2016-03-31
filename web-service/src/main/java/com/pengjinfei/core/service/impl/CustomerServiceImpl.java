package com.pengjinfei.core.service.impl;

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
    public void insertCustomer(Customer customer) {
        customerDao.insert(customer);
        customer.setName(customer.getName()+"Inner");
        innerInsertCustomer(customer);
        throw new RuntimeException("test");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerInsertCustomer(Customer customer) {
        customerDao.insert(customer);
    }

    public Customer getById(String id) {
        Customer customer=new Customer();
        customer.setId(Integer.valueOf(id));
        customer.setName("normal");
        return customer;
    }

    @Transactional
    public void updateByName(String name) {
        customerDao.updateByName(name);
    }


}
