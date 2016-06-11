package com.pengjinfei.common.lock;

/**
 * Created by Pengjinfei on 16/5/28.
 * Description:
 */
public interface PreemptiveLock {

    /**
     * 尝试获得锁
     * @return 是否成功获得锁
     * @param lock
     */
    boolean getLock(String lock);

    /**
     * 释放锁
     * @param lock
     */
    void releaseLock(String lock);
}
