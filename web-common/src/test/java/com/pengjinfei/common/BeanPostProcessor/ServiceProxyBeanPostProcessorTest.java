package com.pengjinfei.common.BeanPostProcessor;

import org.apache.zookeeper.*;
import org.junit.Test;

/**
 * Author: EX-PENGJINFEI001
 * DATE: 2016-02-17
 * Description:
 */
public class ServiceProxyBeanPostProcessorTest {

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
}