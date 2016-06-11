package com.pengjinfei.common.BeanPostProcessor;

import com.pengjinfei.common.lock.Lock;
import com.pengjinfei.common.lock.PreemptiveLock;
import com.pengjinfei.common.lock.impl.ZookeeperPreemptiveLock;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Pengjinfei on 16/6/11.
 * Description:
 */
public class LockPostConstructPostProcessor implements BeanPostProcessor, PriorityOrdered,ApplicationContextAware {

    private transient final Map<String , PreemptiveLock> postConstructLockCache =
            new ConcurrentHashMap<String , PreemptiveLock>(256);

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithLocalMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {

            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Lock lock = method.getAnnotation(Lock.class);
                if (lock != null && method.getAnnotation(PostConstruct.class) != null) {
                    PreemptiveLock preemptiveLock= new ZookeeperPreemptiveLock();
                    String value = lock.value();
                    if (!StringUtils.hasText(value)) {
                        value = beanName + "." + method.getName();
                    }
                    boolean locked = preemptiveLock.getLock(value);
                    if (locked) {
                        postConstructLockCache.put(beanName, preemptiveLock);
                    } else {
                        CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor = (CommonAnnotationBeanPostProcessor) applicationContext.getBean(AnnotationConfigUtils.COMMON_ANNOTATION_PROCESSOR_BEAN_NAME);
                        try {
                            Field lifecycleMetadataCacheField = InitDestroyAnnotationBeanPostProcessor.class.getDeclaredField("lifecycleMetadataCache");
                            lifecycleMetadataCacheField.setAccessible(true);
                            Map map = ((Map) lifecycleMetadataCacheField.get(commonAnnotationBeanPostProcessor));
                            if (map == null) {
                                return;
                            }
                            Object lifecycleMetadata = map.get(bean.getClass());
                            Class<?> lifecycleMetadataClass = lifecycleMetadata.getClass();
                            Field checkedInitMethodsField = lifecycleMetadataClass.getDeclaredField("checkedInitMethods");
                            checkedInitMethodsField.setAccessible(true);
                            Set set = (Set) checkedInitMethodsField.get(lifecycleMetadata);
                            ParameterizedType parameterizedType = (ParameterizedType) checkedInitMethodsField.getGenericType();
                            Class<?> lifecycleElementClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            Field lifecycleElementClassMethodField = lifecycleElementClass.getDeclaredField("method");
                            lifecycleElementClassMethodField.setAccessible(true);
                            Iterator iterator = set.iterator();
                            while (iterator.hasNext()) {
                                Object next = iterator.next();
                                if (lifecycleElementClassMethodField.get(next) == method) {
                                    iterator.remove();
                                }
                            }
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        PreemptiveLock preemptiveLock = postConstructLockCache.get(beanName);
        if (preemptiveLock != null) {
            preemptiveLock.releaseLock();
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
