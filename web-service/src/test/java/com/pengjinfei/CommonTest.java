package com.pengjinfei;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pengjinfei on 16/5/24.
 * Description:
 */
public class CommonTest {

    private List<String> list = new ArrayList<>();

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        CommonTest test=new CommonTest();
        List<String> list = test.getList();
        list.add("123");

        Field field = CommonTest.class.getDeclaredField("list");
        field.setAccessible(true);
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Class<?> aClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        System.out.println(aClass.getName());
    }
}
