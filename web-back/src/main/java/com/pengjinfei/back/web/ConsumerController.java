package com.pengjinfei.back.web;

import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-05-10
 * Description:
 */
@Controller
public class ConsumerController {

    @Autowired
    CustomerService customerService;

    @RequestMapping("/test")
    @ResponseBody
    public Customer getCustomer() {
        return customerService.getById("66");
    }
}
