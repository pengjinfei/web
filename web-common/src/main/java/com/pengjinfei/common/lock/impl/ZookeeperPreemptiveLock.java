package com.pengjinfei.common.lock.impl;

import com.pengjinfei.common.lock.PreemptiveLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pengjinfei on 16/5/28.
 * Description:
 */
@Component("preemptiveLock")
public class ZookeeperPreemptiveLock implements PreemptiveLock, ApplicationContextAware, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ZookeeperPreemptiveLock.class);
    private final String BASE_PATH = "/locks/";
    private transient final Map<String, InterProcessMutex> lockPathMutexCache =
            new ConcurrentHashMap<>(256);
    private CuratorFramework curator;
    private ApplicationContext applicationContext;

    @Override
    public boolean getLock(String lock) {
        if (!StringUtils.hasText(lock)) {
            return false;
        }
        try {
            String path = BASE_PATH + lock;
            InterProcessMutex mutex = lockPathMutexCache.get(lock);
            if (mutex == null) {
                mutex = new InterProcessMutex(curator, path);
                lockPathMutexCache.putIfAbsent(lock, mutex);
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

    @Override
    public void afterPropertiesSet() throws Exception {
        this.curator = applicationContext.getBean("curator", CuratorFramework.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
