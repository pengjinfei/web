package com.pengjinfei.core.service;

import com.pengjinfei.core.po.Customer;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
public interface CustomerService {

    void insertCustomer(Customer customer);

    void innerInsertCustomer(Customer customer);

    Customer getById(String id);

    void updateByName(String name);
}
