package com.pengjinfei.common.BeanPostProcessor;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Pengjinfei on 16/6/11.
 * Description:
 */
public class DubboRefVersionPostProcessor implements BeanPostProcessor,Ordered {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithLocalFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Reference reference = field.getAnnotation(Reference.class);
                if (reference == null|| !"".equals(reference.version())) {
                    return;
                }
                String name = field.getName();
                try {
                    Field declaredAnnotationsField = Field.class.getDeclaredField("declaredAnnotations");
                    declaredAnnotationsField.setAccessible(true);
                    Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) declaredAnnotationsField.get(field);
                    Reference newReference = generateReferenceWithNewVersion(reference, name);
                    annotations.put(Reference.class, newReference);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });
        return bean;
    }

    private Reference generateReferenceWithNewVersion(Reference reference, String name) {
        return new Reference(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return reference.annotationType();
            }

            @Override
            public Class<?> interfaceClass() {
                return reference.interfaceClass();
            }

            @Override
            public String interfaceName() {
                return reference.interfaceName();
            }

            @Override
            public String version() {
                return name;
            }

            @Override
            public String group() {
                return reference.group();
            }

            @Override
            public String url() {
                return reference.url();
            }

            @Override
            public String client() {
                return reference.client();
            }

            @Override
            public boolean generic() {
                return reference.generic();
            }

            @Override
            public boolean injvm() {
                return reference.injvm();
            }

            @Override
            public boolean check() {
                return reference.check();
            }

            @Override
            public boolean init() {
                return reference.init();
            }

            @Override
            public boolean lazy() {
                return reference.lazy();
            }

            @Override
            public boolean stubevent() {
                return reference.stubevent();
            }

            @Override
            public String reconnect() {
                return reference.reconnect();
            }

            @Override
            public boolean sticky() {
                return reference.sticky();
            }

            @Override
            public String proxy() {
                return reference.proxy();
            }

            @Override
            public String stub() {
                return reference.stub();
            }

            @Override
            public String cluster() {
                return reference.cluster();
            }

            @Override
            public int connections() {
                return reference.connections();
            }

            @Override
            public int callbacks() {
                return reference.callbacks();
            }

            @Override
            public String onconnect() {
                return reference.onconnect();
            }

            @Override
            public String ondisconnect() {
                return reference.ondisconnect();
            }

            @Override
            public String owner() {
                return reference.owner();
            }

            @Override
            public String layer() {
                return reference.layer();
            }

            @Override
            public int retries() {
                return reference.retries();
            }

            @Override
            public String loadbalance() {
                return reference.loadbalance();
            }

            @Override
            public boolean async() {
                return reference.async();
            }

            @Override
            public int actives() {
                return reference.actives();
            }

            @Override
            public boolean sent() {
                return reference.sent();
            }

            @Override
            public String mock() {
                return reference.mock();
            }

            @Override
            public String validation() {
                return reference.validation();
            }

            @Override
            public int timeout() {
                return reference.timeout();
            }

            @Override
            public String cache() {
                return reference.cache();
            }

            @Override
            public String[] filter() {
                return reference.filter();
            }

            @Override
            public String[] listener() {
                return reference.listener();
            }

            @Override
            public String[] parameters() {
                return reference.parameters();
            }

            @Override
            public String application() {
                return reference.application();
            }

            @Override
            public String module() {
                return reference.module();
            }

            @Override
            public String consumer() {
                return reference.consumer();
            }

            @Override
            public String monitor() {
                return reference.monitor();
            }

            @Override
            public String[] registry() {
                return reference.registry();
            }
        };
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
