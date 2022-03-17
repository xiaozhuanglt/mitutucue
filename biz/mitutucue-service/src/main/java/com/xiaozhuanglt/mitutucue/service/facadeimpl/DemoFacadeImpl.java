package com.xiaozhuanglt.mitutucue.service.facadeimpl;

import com.xiaozhuanglt.mitutucue.facade.DemoFacade;
import com.xiaozhuanglt.mitutucue.service.interfaces.ServiceDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: dubbo接口测试
 * @author: hxz
 * @create: 2019-05-03 23:30
 **/
public class DemoFacadeImpl implements DemoFacade {

    @Autowired
    ServiceDemo serviceDemo;

    @Override
    public int queryArea(Long areaId) {

        int i = serviceDemo.queryArea(areaId);
        return i;
    }
}
