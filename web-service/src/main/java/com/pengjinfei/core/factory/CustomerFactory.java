package com.pengjinfei.core.factory;

import com.pengjinfei.core.po.Customer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
@Component
public class CustomerFactory implements FactoryBean<Customer> {
    public Customer getObject() throws Exception {
        Customer customer=new Customer();
        customer.setName("factoryMake");
        customer.setAge(30);
        return customer;
    }

    public Class<?> getObjectType() {
        return Customer.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
