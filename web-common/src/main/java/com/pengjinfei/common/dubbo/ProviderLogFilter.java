package com.pengjinfei.common.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.pengjinfei.common.web.utils.MDCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * Created by Pengjinfei on 16/6/27.
 * Description:
 */
@Activate(group = Constants.PROVIDER)
public class ProviderLogFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(ProviderLogFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String mdcData = invocation.getAttachment(MDCUtils.MDC_DATA_KEY);
        if (StringUtils.hasText(mdcData)) {
            MDC.put(MDCUtils.MDC_DATA_KEY,mdcData);
        }
        return invoker.invoke(invocation);
    }
}
