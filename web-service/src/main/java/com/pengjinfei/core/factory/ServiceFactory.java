package com.pengjinfei.core.factory;

import com.pengjinfei.core.service.CustomerService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */

@Service
public class ServiceFactory implements BeanFactoryAware {

    private BeanFactory beanFactory;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    public CustomerService getService(String name) {
        if (name.endsWith("other")) {
            return beanFactory.getBean("otherCustomerService", CustomerService.class);
        } else {
            return beanFactory.getBean("customerService", CustomerService.class);
        }
    }
}
