package com.pengjinfei;

import com.pengjinfei.common.lock.Lock;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Pengjinfei on 16/5/24.
 * Description:
 */
public class CommonTest {

    @Lock("abc")
    private List<String> list = new ArrayList<>();

    @Lock("12")
    private int age;

    public static void main(String[] args) throws Exception {
        CommonTest test = new CommonTest();

        ReflectionUtils.doWithMethods(CommonTest.class, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                System.out.println(method.getDeclaringClass().getName()+"."+method.getName());
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (Class<?> parameterType : parameterTypes) {
                    System.out.println(parameterType.getSimpleName());
                }
            }
        });

        Field ageField = CommonTest.class.getDeclaredField("age");
        System.out.println(ageField.getName());
        ageField.setAccessible(true);
        Field field = Field.class.getDeclaredField("declaredAnnotations");

        CommonTest.class.getAnnotations();
        field.setAccessible(true);
        ageField.getAnnotations();
        Object o = field.get(ageField);
        Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) o;
        Annotation newAnnotation = new Lock() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Lock.class;
            }

            @Override
            public String value() {
                return "abcd";
            }
        };
        annotations.put(Lock.class, newAnnotation);

        Lock ageFieldAnnotation = ageField.getAnnotation(Lock.class);
        System.out.println(ageFieldAnnotation.value());
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
