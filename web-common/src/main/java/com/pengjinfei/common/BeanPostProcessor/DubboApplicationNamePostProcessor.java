package com.pengjinfei.common.BeanPostProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Pengjinfei on 16/5/19.
 * Description: 由于service分布式部署,dubbo的application name冲突,修改beanDefinition中的application name
 *              默认为name_timestamp_hostAddress
 */
public class DubboApplicationNamePostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static Logger logger = LoggerFactory.getLogger(DubboApplicationNamePostProcessor.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName.equals("com.alibaba.dubbo.config.ApplicationConfig")) {
                String oldName = beanDefinition.getPropertyValues().get("name").toString();
                String newName=oldName+"_"+System.currentTimeMillis();
                try {
                    String hostAddress = InetAddress.getLocalHost().getHostAddress();
                    newName+="_"+hostAddress;
                } catch (UnknownHostException e) {
                    //ignore
                }

                beanDefinition.getPropertyValues().removePropertyValue("name");
                beanDefinition.getPropertyValues().addPropertyValue("name",newName);

                logger.info("Find dubbo application name:\""+oldName+"\", replaced with \""+newName+"\"");

                break;
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
