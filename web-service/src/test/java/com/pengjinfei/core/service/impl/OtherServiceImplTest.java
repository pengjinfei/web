package com.pengjinfei.core.service.impl;

import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import com.pengjinfei.core.service.OtherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Pengjinfei on 16/5/22.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class OtherServiceImplTest {

    @Autowired
    OtherService otherService;

    @Autowired
    CustomerService customerService;

    @Value("zookeeper.url")
    String url;

    @Test
    public void otherInsert() throws Exception {
        Customer customer=new Customer();
        customer.setName("pjf");
        customer.setAge(28);
//        customerService.insertCustomer(customer);
    }

    @Test
    public void getById() throws Exception {
        Customer byId = customerService.getById("100000000000000");
        System.out.println(byId);
    }

    @Test
    public void testProperties() {
        System.out.println(url);
    }
}