package com.pengjinfei.common.lock.impl;

import com.pengjinfei.common.lock.PreemptiveLock;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pengjinfei on 16/5/28.
 * Description:
 */
public class ZookeeperPreemptiveLock implements PreemptiveLock {

    private final static String BASE_PATH = "/locks/";
    private static Logger logger = LoggerFactory.getLogger(ZookeeperPreemptiveLock.class);
    private transient final Map<String, InterProcessMutex> lockPathMutexCache =
            new ConcurrentHashMap<>(256);

    private static CuratorFramework curatorFramework;

    private static String zookeeperUrl;

    static{
        try {
            InputStream resourceAsStream = ZookeeperPreemptiveLock.class.getClassLoader().getResourceAsStream("common.properties");
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            zookeeperUrl = properties.getProperty("zookeeper.url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperUrl, retryPolicy);
        curatorFramework.start();
    }

    @Override
    public boolean getLock(String lock) {
        if (!StringUtils.hasText(lock)) {
            return false;
        }
        try {
            String path = BASE_PATH + lock;
            InterProcessMutex mutex = lockPathMutexCache.get(lock);
            if (mutex == null) {
                if (lockPathMutexCache.get(lock) != null) {
                    mutex = new InterProcessMutex(curatorFramework, path);
                    lockPathMutexCache.putIfAbsent(lock, mutex);
                }
            }
            mutex = lockPathMutexCache.get(lock);
            if (mutex != null && mutex.acquire(1, TimeUnit.SECONDS)) {
                logger.info("获得锁:" + path + "成功.");
                return true;
            }
        } catch (Exception e) {
            logger.error("尝试获得锁异常.", e);
        }
        logger.info("获得锁:" + BASE_PATH + lock + "失败.");
        return false;
    }

    @Override
    public void releaseLock(String lock) {
        if (!StringUtils.hasText(lock)) {
            return;
        }
        InterProcessMutex mutex = lockPathMutexCache.get(lock);
        if (mutex != null) {
            try {
                mutex.release();
                logger.debug("释放锁:" + BASE_PATH + lock + "成功.");
            } catch (Exception e) {
                logger.error("关闭InterProcessMutex异常.", e);
            }
        }
    }
}
