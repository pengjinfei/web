package com.pengjinfei;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by Pengjinfei on 16/5/24.
 * Description:
 */
public class CommonTest {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CommonTest.class);
        enhancer.setCallback(new MethodInterceptorImpl());

        CommonTest test = (CommonTest) enhancer.create();
        test.test();
    }

    public void test() {
        System.out.println("CommonTest.test");
        testNothin();
    }

    public void testNothin() {

    }

    private static class MethodInterceptorImpl implements MethodInterceptor{

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            System.out.println("before");
            Object result = methodProxy.invokeSuper(o, objects);
            System.out.println("after");
            return result;
        }
    }
}
