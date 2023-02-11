package com.xiaozhuanglt.mitutucue.service.impl;

import com.xiaozhuanglt.mitutucue.common.HttpRequestUtil;
import com.xiaozhuanglt.mitutucue.common.IncrementalEncoder;
import com.xiaozhuanglt.mitutucue.model.AmapApiDistrictJsonRO;
import com.xiaozhuanglt.mitutucue.service.interfaces.AmapService;
import net.sf.cglib.beans.BeanMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Auther: chejiebin
 * @Date: 2019/1/22 18:27
 * @Description: 高德地图相关接口
 */
@Service("amapService")
public class AmapServiceImpl implements AmapService {


    /**
     * @author hxz
     * @date  2019/1/23/023 14:18
     * @param requestUrl 请求路径
     * @param requestMethod 请求方式 POST、GET
     * @return {@link List<  AmapApiDistrictJsonRO  >}
     */
    @Override
    public List<AmapApiDistrictJsonRO> downloadAmapDistrict(String requestUrl, String requestMethod,Long virtualParentId) {
        List<AmapApiDistrictJsonRO> amapApiDistrictJsonROS = new ArrayList<>();
        JSONArray jsonArray = HttpRequestUtil.httpsRequest(requestUrl, requestMethod);
        traversejSONArray(amapApiDistrictJsonROS,jsonArray,virtualParentId);
        return amapApiDistrictJsonROS;
    }

    /**
     * @param requestUrl
     * @param requestMethod
     * @return {@link String}
     * @author hxz
     * @date 2019/1/25/025 10:47
     */
    @Override
    public String downloadStreetCode(String requestUrl, String requestMethod,String adcode) {

        JSONArray jsonArray = HttpRequestUtil.httpsRequest(requestUrl, requestMethod);
        String towncode = getTowncode(jsonArray.getJSONObject(0),adcode);
        return towncode;
    }

    /**
     * @param requestUrl
     * @param requestMethod
     * @return {@link String}
     * <p>
     * 根据地区名称查询地区经纬度
     * @author hxz
     * @date 2019/1/25/025 17:04
     */
    @Override
    public List<String> getAmapCoords(String requestUrl, String requestMethod) {

        List<String> coords = new ArrayList<>();
        JSONArray jsonArray = HttpRequestUtil.httpsRequest(requestUrl, requestMethod);
        if (!jsonArray.isEmpty()){
            for (int i=0; i<jsonArray.size(); i++){
                String coord = getCoords(jsonArray.getJSONObject(i));
                if (coords != null){
                    coords.add(coord);
                }
            }
        }
        return coords;
    }

    /**
     * @param requestUrl
     * @param requestMethod
     * @return {@link String}
     * <p>
     * 根据地区名称上级上级adcode查询地区经纬度
     * @author hxz
     * @date 2019/1/25/025 17:04
     */
    @Override
    public List<String> getAmapCoordsByNameAndAdcode(String requestUrl, String requestMethod) {
        List<String> amapCoords = getAmapCoords(requestUrl, requestMethod);
        return amapCoords;
    }

    /**
     * @author hxz
     * @date  2019/1/24/024 17:02
     * @param amapApiDistrictJsonROS
     * @param jsonArrays
     * @return {@link }
     *
     * 遍历JsonArray
     */
    public void traversejSONArray(List<AmapApiDistrictJsonRO> amapApiDistrictJsonROS,JSONArray jsonArrays,Long virtualParentId){

        if (!jsonArrays.isEmpty()){
            for (int i=0; i<jsonArrays.size(); i++){

                //声明装配对象
                AmapApiDistrictJsonRO amapApiDistrictJsonRO = new AmapApiDistrictJsonRO();
                BeanMap beanMap = BeanMap.create(amapApiDistrictJsonRO);

                JSONObject jsonObject = jsonArrays.getJSONObject(i);
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()){
                    String key = iterator.next();
                    Object o = jsonObject.get(key);
                    //内部嵌套，递归解析
                    if (o instanceof JSONArray){
                        if (!((JSONArray) o).isEmpty()){
                            traversejSONArray(amapApiDistrictJsonROS,(JSONArray) o,beanMap.get("virtualId") == null? virtualParentId : (Long) beanMap.get("virtualId"));
                        }
                    }else {
                        //判断组装属性
                        if (key.contains("citycode") || key.contains("adcode") || key.contains("name") || key.contains("level") || key.contains("center")){
                            beanMap.put(key,o);
                            if (beanMap.get("virtualId") == null){
                                beanMap.put("virtualId", IncrementalEncoder.getEncoder());
                                beanMap.put("virtualParentId",virtualParentId);
                            }
                        }
                    }
                }
                amapApiDistrictJsonROS.add(amapApiDistrictJsonRO);
            }
        }
    }

    /**
     * 获取街道code
     * @param jsonObject
     * @return
     */
    public String getTowncode(JSONObject jsonObject,String adcode){

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()){
            String key = iterator.next();
            Object o = jsonObject.get(key);
            if (o instanceof JSONObject){
                String towncode = getTowncode((JSONObject) o,adcode);
                return towncode;
            }
            if (key.contains("adcode")){
                if (o == null || !o.equals(adcode)){
                    return null;
                }
            }
            if (key.contains("towncode")){
                if (o instanceof String){
                    return (String)o;
                }else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 获取经纬度
     */
    public String getCoords(JSONObject jsonObject){

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()){
            String key = iterator.next();
            Object o = jsonObject.get(key);
            if (o instanceof JSONArray){
                if (!((JSONArray) o).isEmpty()){
                    String coords = getCoords(((JSONArray) o).getJSONObject(0));
                    return coords;
                }
            }
            if (key.contains("location")){
                return (String) o;
            }
        }
        return null;
    }

}
