package com.xiaozhuanglt.mitutucue.controller;

import com.xiaozhuanglt.mitutucue.service.interfaces.ServiceDemo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 框架测试
 * @author: hxz
 * @create: 2019-04-24 17:55
 **/

@Api(tags = "框架测试")
@RestController
@RequestMapping("mitutucueApi/demo")
public class DemoController {

    @Autowired
    ServiceDemo serviceDemo;

    /**
     * @author hxz
     * @date  2019/4/25 11:02
     * @Description:
     * @Param: [areaId]
     * @return {@link int}
     */
    @ApiOperation("查询地址")
    @GetMapping("/queryArea")
    public int queryArea(Long areaId){
        int i = serviceDemo.queryArea(areaId);
        return i;
    }
}
