package com.xiaozhuanglt.mitutucue.service.impl;

import com.xiaozhuanglt.mitutucue.dao.DemoDao;
import com.xiaozhuanglt.mitutucue.service.interfaces.ServiceDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 框架测试
 * @author: hxz
 * @create: 2019-04-24 17:56
 **/

@Service("serviceDemo")
public class ServiceDemoImpl implements ServiceDemo {

    Logger logger = LoggerFactory.getLogger(ServiceDemoImpl.class);

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

        try {
            int i = demoDao.selectAreaById(areaId);
            return i;
        }catch (Exception e){
            logger.error(e.getMessage());
            System.out.println(e);
            return -1;
        }

    }
}
