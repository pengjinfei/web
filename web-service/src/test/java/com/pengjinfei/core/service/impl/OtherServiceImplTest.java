package com.pengjinfei.core.service.impl;

import com.pengjinfei.core.factory.ServiceFactory;
import com.pengjinfei.core.po.Customer;
import com.pengjinfei.core.service.CustomerService;
import com.pengjinfei.core.service.OtherService;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Autowired
    CuratorFramework curatorFramework;

    @Autowired
    ServiceFactory serviceFactory;

    @Test
    public void otherInsert() throws Exception {
        Customer customer=new Customer();
        customer.setName("pjf");
        customer.setAge(28);
        customerService.insertCustomer(customer);
    }

    @Test
    public void getById() throws Exception {
        Customer byId = customerService.getById("100000000000000");
        System.out.println(byId);
        customerService.getById("100000000000000");
    }

    @Test
    public void testProperties() throws Exception {
        Pattern pattern=Pattern.compile("%3D(.*)\\$");
        List<String> bytes = curatorFramework.getChildren().forPath("/dubbo/com.pengjinfei.core.service.CustomerService/providers");
        for (String aByte : bytes) {
            System.out.println(aByte);
            Matcher matcher = pattern.matcher(aByte);
            if (matcher.matches()) {
                System.out.println("find version "+matcher.group(1));
            }
        }
    }


    @Test
    public void testQuartz() throws Exception {
        Scheduler scheduler = serviceFactory.getScheduler();
        List<String> jobGroupNames = scheduler.getJobGroupNames();
        Set<JobKey> jobKeys = scheduler.getJobKeys(null);
        for (JobKey jobKey : jobKeys) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            System.out.println("find jobKey "+jobKey+" 分割 "+jobDetail.toString());
        }
        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(null);
        for (TriggerKey triggerKey : triggerKeys) {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger instanceof CronTriggerImpl) {
                CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                System.out.println(cronExpression+" 分割 "+ cronTrigger.getName());
            }
        }
    }
}