package com.xiaozhuanglt.mitutucue.dao;

import org.apache.ibatis.annotations.Param;

/**
 * @description: 框架测试
 * @author: hxz
 * @create: 2019-04-25 10:38
 **/
public interface DemoDao {

    int selectAreaById(@Param("areaId")Long areaId);
}
