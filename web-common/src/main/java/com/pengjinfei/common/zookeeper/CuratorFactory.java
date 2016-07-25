package com.pengjinfei.common.zookeeper;

import com.pengjinfei.common.BeanPostProcessor.ParameterizedProperties;
import com.pengjinfei.common.lock.impl.ZookeeperPreemptiveLock;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Pengjinfei on 16/7/4.
 * Description:
 */
@Component("curator")
public class CuratorFactory implements FactoryBean<CuratorFramework> {
    @Override
    public CuratorFramework getObject() throws Exception {
        String zookeeperUrl;
        try {
            InputStream resourceAsStream = ZookeeperPreemptiveLock.class.getClassLoader().getResourceAsStream("common.properties");
            Properties properties = new ParameterizedProperties();
            properties.load(resourceAsStream);
            zookeeperUrl = properties.getProperty("zookeeper.url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zookeeperUrl, retryPolicy);
        curatorFramework.start();
        return curatorFramework;
    }

    @Override
    public Class<?> getObjectType() {
        return CuratorFramework.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
