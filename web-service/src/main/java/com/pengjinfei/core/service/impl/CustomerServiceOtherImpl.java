package com.pengjinfei.core.service.impl;

import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import org.springframework.stereotype.Service;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
@Service("otherCustomerService")
public class CustomerServiceOtherImpl implements CustomerService {
    public void insertCustomer(Customer customer) {

    }

    public void innerInsertCustomer(Customer customer) {

    }

    public Customer getById(String id) {
        Customer customer=new Customer();
        customer.setId(Integer.valueOf(id));
        customer.setName("other");
        return customer;

    }

    public void updateByName(String name) {

    }
}
