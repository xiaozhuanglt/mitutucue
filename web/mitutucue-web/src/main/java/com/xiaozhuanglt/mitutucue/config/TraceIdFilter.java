/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.xiaozhuanglt.mitutucue.config;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @author hxz（Eason）
 * @version $Id: TraceIdFiltger.java, v 0.1 2023-01-16 00:16 hxz（Eason） Exp $$
 */
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER})
public class TraceIdFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcServiceContext rpcServiceContext = RpcContext.getServiceContext();

        String traceId;
        if (null == rpcServiceContext.getUrl()){
            return invoker.invoke(invocation);
        }
        if (StringUtils.isEmpty(MDC.get("MDCID"))){
            traceId = rpcServiceContext.getAttachment("MDCID");
            if (traceId == null){
                traceId = UUID.randomUUID().toString();
                rpcServiceContext.setAttachment("MDCID",traceId);
            }
            MDC.put("MDCID", traceId);
        }else {
            traceId = rpcServiceContext.getAttachment("MDCID");
            if (traceId == null){
                rpcServiceContext.setAttachment("MDCID",MDC.get("MDCID"));
            }
        }
        return invoker.invoke(invocation);
    }
}
