package com.pengjinfei.common.BeanPostProcessor;

import org.apache.zookeeper.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181",
                3000, new Watcher() {
            // 监控所有被触发的事件
            public void process(WatchedEvent event) {
                System.out.println("已经触发了" + event.getType() + "事件！");
            }
        });
        // 创建一个目录节点
        zk.delete("/testRootPath",-1);
        zk.create("/test", "good".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        Thread.sleep(5000);
    }

    @Test
    public void testSpring(){

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