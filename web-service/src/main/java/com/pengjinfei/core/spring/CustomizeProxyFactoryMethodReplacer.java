package com.pengjinfei.core.spring;

import com.pengjinfei.common.lock.LockAdvisor;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.support.MethodReplacer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by Pengjinfei on 16/5/30.
 * Description:
 */
@Component("customizeProxyFactoryMethodReplacer")
public class CustomizeProxyFactoryMethodReplacer implements MethodReplacer {
    @Override
    public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
        ProxyFactory factory = (ProxyFactory) args[0];
        Advisor[] advisors = factory.getAdvisors();
        if (advisors.length <= 1) {
            return null;
        }
        int i=0;
        for (; i < advisors.length; i++) {
            if (advisors[i] instanceof LockAdvisor) {
                break;
            }
        }
        Advisor temp = advisors[i];
        advisors[i] = advisors[0];
        advisors[0]=temp;
        return null;
    }
}
