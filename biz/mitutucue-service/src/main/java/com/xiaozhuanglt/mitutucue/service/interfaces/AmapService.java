package com.xiaozhuanglt.mitutucue.service.interfaces;

import com.xiaozhuanglt.mitutucue.model.AmapApiDistrictJsonRO;

import java.util.List;

/**
 * @Auther: chejiebin
 * @Date: 2019/1/22 18:27
 * @Description: 高德地图相关接口
 */
public interface AmapService {


    /**
     * @author hxz
     * @date  2019/1/23/023 14:18
     * @param requestUrl 请求路径
     * @param requestMethod 请求方式 POST、GET
     * @return {@link List < AmapApiDistrictJsonRO >}
     *
     * 下载高德行政区划，省、市、区
     */
    List<AmapApiDistrictJsonRO> downloadAmapDistrict(String requestUrl, String requestMethod,Long virtualParentId);

    /**
     * @author hxz
     * @date  2019/1/25/025 10:53
     * @param requestUrl
     * @param requestMethod
     * @return {@link String}
     */
    String downloadStreetCode(String requestUrl, String requestMethod, String adcode);

    /**
     * @author hxz
     * @date  2019/1/25/025 17:04
     * @param requestUrl
     * @param requestMethod
     * @return {@link String}
     *
     * 根据地区名称查询地区经纬度
     */
    List<String> getAmapCoords(String requestUrl, String requestMethod);

    /**
     * @author hxz
     * @date  2019/1/25/025 17:04
     * @param requestUrl
     * @param requestMethod
     * @return {@link String}
     *
     * 根据地区名称上级上级adcode查询地区经纬度
     */
    List<String> getAmapCoordsByNameAndAdcode(String requestUrl, String requestMethod);
}
