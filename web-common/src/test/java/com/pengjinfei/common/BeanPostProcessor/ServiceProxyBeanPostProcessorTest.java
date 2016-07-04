package com.pengjinfei.common.BeanPostProcessor;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Author: EX-PENGJINFEI001
 * DATE: 2016-02-17
 * Description:
 */
public class ServiceProxyBeanPostProcessorTest {

    private BlockingQueue<String> verifyCodeCache=new LinkedBlockingDeque<String>(200);

    private volatile boolean isCreating=false;

    private int warningLevel=100;

    private int creatingStep=200;

    private Logger logger = LoggerFactory.getLogger(ServiceProxyBeanPostProcessorTest.class);

    @Test
    public void testZookeeper() throws Exception{
        String zookeeperUrl = "120.25.95.166:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zookeeperUrl, retryPolicy);
        curatorFramework.start();
        List<String> bytes = curatorFramework.getChildren().forPath("/dubbo/com.pengjinfei.core.service.CustomerService/providers");
        for (String aByte : bytes) {
            System.out.println(aByte);
        }
    }

    @Test
    public void testBlockingQueue() throws InterruptedException {
        for (int i = 0; i < 100; i++) {

            System.out.println(getVerifyCode());
        }
    }

    private String getVerifyCode() throws InterruptedException {
        if (!isCreating && verifyCodeCache.size() < 10) {
            isCreating=true;
            System.out.println("start to creating verifyCode");
            new Thread(){
                @Override
                public void run() {
                    Random random = new Random();
                    for (int i = 0; i < 20; i++) {
                        try {
                            String temp= String.valueOf(random.nextInt(10000));
                            verifyCodeCache.put(temp);
                            System.out.println("put "+temp);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("finished creating");
                    isCreating=false;
                }
            }.start();
        }
        return verifyCodeCache.take();
    }
}