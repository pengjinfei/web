package com.pengjinfei.common.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.pengjinfei.common.web.utils.MDCUtils;

/**
 * Created by Pengjinfei on 16/6/27.
 * Description:
 */
@Activate(group = Constants.CONSUMER)
public class ConsumerLogFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (invocation instanceof RpcInvocation) {
            RpcInvocation rpcInvocation = (RpcInvocation) invocation;
            rpcInvocation.setAttachmentIfAbsent(MDCUtils.MDC_DATA_KEY,MDCUtils.get());
        }
        return invoker.invoke(invocation);
    }
}
