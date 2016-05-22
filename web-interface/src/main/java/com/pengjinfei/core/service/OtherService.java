package com.pengjinfei.core.service;

import com.pengjinfei.core.po.Customer;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
public interface OtherService {
    void otherInsert(Customer customer);

    Customer getById(String id);

}
