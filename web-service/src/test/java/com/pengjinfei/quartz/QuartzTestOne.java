package com.pengjinfei.quartz;

import com.pengjinfei.common.quartz.MDCMethodInvokingJob;
import org.junit.Test;

/**
 * Created by Pengjinfei on 16/6/18.
 * Description:
 */
public class QuartzTestOne {

    @Test
    public void testQuick() {
        String simpleName = MDCMethodInvokingJob.class.getSimpleName();
        System.out.println(simpleName);
    }

}
