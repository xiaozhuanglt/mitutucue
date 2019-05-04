package com.xiaozhuanglt.mitutucue.model;

import lombok.Data;

/**
 * 高德行政区域查询json数据
 */
@Data
public class AmapApiDistrictJsonRO {

    /**
     * 行政编码
     */
    private String citycode;
    /**
     * 区域编码
     */
    private String adcode;
    /**
     * 地区名称
     */
    private String name;
    /**
     * 级别
     */
    private String level;
    /**
     * 中心点坐标
     */
    private String center;
    /**
     * 详细code
     */
    private String detailCode;
    /**
     * 修正经纬度
     */
    private String alterCoords;
}
