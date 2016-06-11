package com.pengjinfei.back.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import com.pengjinfei.core.service.OtherService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-05-10
 * Description:
 */
@Controller
public class ConsumerController {

    @Reference
    OtherService otherServiceImpl;

    @Reference
    CustomerService customerService;

    @Reference
    CustomerService otherCustomerService;

    @RequestMapping("/test/{id}")
    @ResponseBody
    public Customer getCustomer(@PathVariable("id") String  id) {
        return otherServiceImpl.getById(id);
    }

    @RequestMapping("/first/{id}")
    @ResponseBody
    public Customer getCustomerByCustomerService(@PathVariable("id") String  id) {
        return customerService.getById(id);
    }

    @RequestMapping("/second/{id}")
    @ResponseBody
    public Customer getCustomerByOtherCustomerService(@PathVariable("id") String  id) {
        return otherCustomerService.getById(id);
    }
}
