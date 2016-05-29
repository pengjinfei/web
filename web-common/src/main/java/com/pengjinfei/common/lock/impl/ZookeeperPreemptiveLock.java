package com.pengjinfei.common.lock.impl;

import com.pengjinfei.common.lock.PreemptiveLock;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Pengjinfei on 16/5/28.
 * Description:
 */
public class ZookeeperPreemptiveLock implements PreemptiveLock {

    private static Logger logger = LoggerFactory.getLogger(ZookeeperPreemptiveLock.class);

    private final String BASE_PATH = "/locks/";

    private InterProcessMutex mutex;

    @Override
    public boolean getLock(String lock) {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
            curatorFramework.start();
            mutex = new InterProcessMutex(curatorFramework, BASE_PATH + lock);
            if (mutex.acquire(1, TimeUnit.SECONDS)) {
                logger.info("获得锁:"+BASE_PATH+lock+"成功.");
                return true;
            }
        } catch (Exception e) {
            logger.error("尝试获得锁异常.",e);
        }
        logger.info("获得锁:"+BASE_PATH+lock+"失败.");
        return false;
    }

    @Override
    public void releaseLock() {
        if (mutex != null) {
            try {
                mutex.release();
            } catch (Exception e) {
                logger.error("关闭InterProcessMutex异常.",e);
            }
        }
    }
}
