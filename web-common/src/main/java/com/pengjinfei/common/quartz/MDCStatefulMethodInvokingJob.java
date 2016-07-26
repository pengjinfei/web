package com.pengjinfei.common.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * Created by Pengjinfei on 16/7/26.
 * Description:
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MDCStatefulMethodInvokingJob extends MDCMethodInvokingJob {
}
