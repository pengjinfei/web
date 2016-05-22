package com.pengjinfei.core.factory;

import com.pengjinfei.core.po.Customer;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by Pengjinfei on 16/5/12.
 * Description:
 */
public class CustomerFactoryTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        Customer customer = context.getBean(Customer.class);
        System.out.println(customer);
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        System.out.println(Arrays.toString(beanDefinitionNames));
        String[] beanNamesForType = context.getBeanNamesForType(Customer.class);
        System.out.println(Arrays.toString(beanNamesForType));
    }

    @Test
    public void testValue() {
        String value = "customerService123";
        Pattern pattern = Pattern.compile("\\d*");
        String all = value.replaceAll("\\d*", "");
        System.out.println(all);
    }

}