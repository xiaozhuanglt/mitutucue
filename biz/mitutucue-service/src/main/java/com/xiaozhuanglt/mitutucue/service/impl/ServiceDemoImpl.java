package com.xiaozhuanglt.mitutucue.service.impl;

import com.xiaozhuanglt.mitutucue.dao.DemoDao;
import com.xiaozhuanglt.mitutucue.service.interfaces.ServiceDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 框架测试
 * @author: hxz
 * @create: 2019-04-24 17:56
 **/

@Service("serviceDemo")
public class ServiceDemoImpl implements ServiceDemo {

    @Autowired
    DemoDao demoDao;

    /**
     * @param areaId
     * @return {@link int}
     * @author hxz
     * @date 2019/4/25 10:05
     * @Description: 查询地址信息
     * @Param: [areaId]
     */
    @Override
    public int queryArea(Long areaId) {
        int i = demoDao.selectAreaById(areaId);
        return i;

    }
}
