package com.pengjinfei.core.dao;

import com.pengjinfei.core.po.Customer;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
public interface CustomerDao {
    void insert(Customer customer);

    void updateByName(String name);

    Customer getById(String id);
}
