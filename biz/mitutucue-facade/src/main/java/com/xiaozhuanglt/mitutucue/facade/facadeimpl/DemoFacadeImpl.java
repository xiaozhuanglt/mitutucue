package com.xiaozhuanglt.mitutucue.facade.facadeimpl;

import com.xiaozhuanglt.mitutucue.facade.facade.DemoFacade;
import com.xiaozhuanglt.mitutucue.service.interfaces.ServiceDemo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: dubbo接口测试
 * @author: hxz
 * @create: 2019-05-03 23:30
 **/
public class DemoFacadeImpl implements DemoFacade {

    @Autowired
    ServiceDemo serviceDemo;

    public int queryArea(Long areaId) {

        int i = serviceDemo.queryArea(areaId);
        return i;
    }
}
