package com.pengjinfei.common.lock;

import com.pengjinfei.common.lock.impl.ZookeeperPreemptiveLock;

/**
 * Created by Pengjinfei on 16/6/12.
 * Description:
 */
public class PreemptiveLockFactory {

    static class  Nested{
        private static PreemptiveLock preemptiveLock=new ZookeeperPreemptiveLock();
    }

    public static PreemptiveLock getPreemptiveLock() {
        return Nested.preemptiveLock;
    }
}
