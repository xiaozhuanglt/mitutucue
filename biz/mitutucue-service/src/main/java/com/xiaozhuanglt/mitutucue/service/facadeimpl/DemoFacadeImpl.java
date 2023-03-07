package com.xiaozhuanglt.mitutucue.service.facadeimpl;

import com.xiaozhuanglt.mitutucue.facade.DemoFacade;
import com.xiaozhuanglt.mitutucue.service.interfaces.ServiceDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: dubbo接口测试
 * @author: hxz
 * @create: 2019-05-03 23:30
 **/
public class DemoFacadeImpl implements DemoFacade {
    Logger logger = LoggerFactory.getLogger(DemoFacadeImpl.class);

    @Autowired
    ServiceDemo serviceDemo;

    @Override
    public int queryArea(Long areaId) {
        try {
            System.out.println("=========1:"+ MDC.get("MDCID"));
            logger.info("收到queryArea 的查询请求，areaId:"+areaId);
            int i = serviceDemo.queryArea(areaId);
            System.out.println("=========2:"+MDC.get("MDCID"));
            return i;
        } catch (Exception e) {
            logger.error("查询queryArea错误：",e);
        }
        return -1;
    }
}
