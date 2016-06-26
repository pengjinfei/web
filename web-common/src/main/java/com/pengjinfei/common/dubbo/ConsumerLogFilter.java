package com.pengjinfei.common.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by Pengjinfei on 16/6/27.
 * Description:
 */
@Activate(group = Constants.CONSUMER)
public class ConsumerLogFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(ConsumerLogFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String test = invocation.getAttachment("test");
        if (StringUtils.hasText(test)) {
            logger.info("get test in invocation:"+test);
        }
        return invoker.invoke(invocation);
    }
}
