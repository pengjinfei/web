package com.pengjinfei.common.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;

/**
 * Created by Pengjinfei on 16/6/27.
 * Description:
 */
@Activate(group = Constants.PROVIDER)
public class ProviderLogFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (invocation instanceof RpcInvocation) {
            RpcInvocation rpcInvocation = (RpcInvocation) invocation;
            rpcInvocation.setAttachmentIfAbsent("test","pjf");
        }
        return invoker.invoke(invocation);
    }
}
