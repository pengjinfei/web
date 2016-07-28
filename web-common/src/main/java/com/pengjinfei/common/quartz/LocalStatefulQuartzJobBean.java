package com.pengjinfei.common.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * Created by Pengjinfei on 16/7/28.
 * Description:
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LocalStatefulQuartzJobBean extends LocalQuartzJobBean {
}
