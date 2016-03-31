package com.pengjinfei.core.service.impl;

import com.pengjinfei.core.dao.CustomerDao;
import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import com.pengjinfei.core.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
@Service
public class OtherServiceImpl implements OtherService {


    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustomerService customerService;


    @Transactional
    public void otherInsert(Customer customer) {
        customerDao.insert(customer);
        customer.setName(customer.getName()+"other");
        customerService.innerInsertCustomer(customer);
    }
}
