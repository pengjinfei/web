package com.pengjinfei.core.service.impl;

import com.pengjinfei.core.factory.ServiceFactory;
import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class CustomerServiceImplTest {

    @Qualifier("customer")
    @Autowired
    private Customer customer;
    @Qualifier("customerFactory")
    @Autowired
    private Customer customerFactory;

    @Autowired
    private ServiceFactory serviceFactory;

    @Test
    public void insertCustomer() throws Exception {
        Customer customer=new Customer();
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("mmss");
        String dateString = format.format(date);
        customer.setName(dateString);
        customer.setAge(28);
        CustomerService customerService = serviceFactory.getService("customerService");
        customerService.innerInsertCustomer(customer);
    }

    @Test
    public void testFactory() {
        System.out.println(customerFactory);
    }

    @Test
    public void testBeanFactory() {
        CustomerService other = serviceFactory.getService("other");
        System.out.println(other.getById("123"));
    }

    @Test
    public void testUpdate() {
        CustomerService customerService = serviceFactory.getService("customerService");
        customerService.updateByName("5318");
    }
}