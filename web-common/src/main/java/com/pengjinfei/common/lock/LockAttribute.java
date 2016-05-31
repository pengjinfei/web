package com.pengjinfei.common.lock;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Pengjinfei on 16/5/31.
 * Description:
 */
public class LockAttribute {

    private Map<MethodClassKey, String> attributeCache = new ConcurrentHashMap<>(128);

    public String getLockPath(Method method, Class<?> targetClass) {
        MethodClassKey cacheKey = new MethodClassKey(method, targetClass);
        return this.attributeCache.get(cacheKey);
    }

    public void cache(Method method, Class<?> targetClass,String value) {
        MethodClassKey cacheKey = new MethodClassKey(method, targetClass);
        attributeCache.putIfAbsent(cacheKey, value);
    }
}
