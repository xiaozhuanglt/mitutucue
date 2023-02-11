package com.xiaozhuanglt.mitutucue.controller;

import com.xiaozhuanglt.mitutucue.common.ExcelUtils;
import com.xiaozhuanglt.mitutucue.common.ResponseHeaderUtil;
import com.xiaozhuanglt.mitutucue.model.AmapApiDistrictJsonRO;
import com.xiaozhuanglt.mitutucue.service.interfaces.AmapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 高德地图服务
 */
@Api(tags = "高德api服务")
@RestController
@RequestMapping("/tmsApi/amap")
public class AmapServiceController {

    @Autowired
    AmapService amapService;

    final String[] lineTransportHeaders= {"行政编码","区域编码","地区名称","级别","中心点坐标","街道code","修正经纬度","虚拟主键","虚拟父主键"};
    final String[] lineTransportParams= {"citycode","adcode","name","level","center","detailCode","alterCoords","virtualId","virtualParentId"};

    @ApiOperation("高德下载行政区域编码")
    @GetMapping("/downloadAmapDistrict")
    @ApiParam(name = "keywords",value = "关键字，地区关键字",required = true)
    public void downloadAmapDistrict(HttpServletResponse response,String keywords, String  subdistrict) throws UnsupportedEncodingException {


        //需要把汉字转成UTF-8 编码
        String keyEncode = null;
        if (keywords != null) {
            keyEncode = URLEncoder.encode(keywords, "UTF-8");
        }
        String key = "7d5c4e2d4aa475b9682aa7c15217e84b";
        String requestUrl = "https://restapi.amap.com/v3/config/district?keywords="+keyEncode+"&subdistrict="+subdistrict+"&key="+key;
        String requestMethod = "GET";

        List<AmapApiDistrictJsonRO> amapApiDistrictJsonROS = amapService.downloadAmapDistrict(requestUrl, requestMethod,Long.valueOf(0));
        //生成Excel
        try {
            if (createExcel(response, amapApiDistrictJsonROS,keywords)) return;
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param response
     * @throws UnsupportedEncodingException
     */
    @ApiOperation("按照省份下载行政编码")
    @GetMapping("downloadAmapDistrictByProvince")
    public void downloadAmapDistrictByProvince(HttpServletResponse response) throws UnsupportedEncodingException {

        String[] provinces = { "北京市","天津市","河北省","山西省","内蒙古自治区","辽宁省","吉林省","黑龙江省","上海市","江苏省","浙江省","安徽省","福建省","江西省","山东省","河南省","湖北省","湖南省","广东省","广西壮族自治区","海南省","重庆市","四川省","贵州省","云南省","西藏自治区","陕西省","甘肃省","青海省","宁夏回族自治区","新疆维吾尔自治区","台湾省","香港特别行政区","澳门特别行政区"};

        String[] testProvinces = {"山西省","内蒙古自治区", "广东省","河南省", "5黑龙江省", "6新疆维吾尔自治区", "7湖北省", "8辽宁省", "9山东省", "10陕西省", "11上海市", "12贵州省",
                "13重庆市", "14西藏自治区", "15安徽省", "16福建省", "17湖南省", "18海南省", "19江苏省", "20青海省", "21广西壮族自治区", "22宁夏回族自治区", "23江西省", "24浙江省", "25河北省",
                "澳门特别行政区", "台湾省", "香港特别行政区", "甘肃省",};

        List<AmapApiDistrictJsonRO> amapApiDistrictJsonROS = new ArrayList<>();
        for(int i=0;i<provinces.length;i++){
            try {
                downloadStreetCode(response,provinces[i],"3",amapApiDistrictJsonROS);
                System.out.print("完成"+provinces[i]+"导出");
            }catch (Exception e){
                System.out.print(provinces[i]+"未完成"+e);
            }
        }
        //生成Excel
        try {

            createExcel(response, amapApiDistrictJsonROS,"高德省市区街道数据");
        }catch (Exception e) {
            e.printStackTrace();
        }
        //生成文件流
        try {
            createText(amapApiDistrictJsonROS,"高德省市区街道数据");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("查询高德四级行政区划")
    @GetMapping("/downloadStreetCode")
    public void downloadStreetCode(HttpServletResponse response,String keywords, String  subdistrict,List<AmapApiDistrictJsonRO> amapApiDistrictJsonSum) throws UnsupportedEncodingException {

        //需要把汉字转成UTF-8 编码
        String keyEncode = null;
        if (keywords != null) {
            keyEncode = URLEncoder.encode(keywords, "UTF-8");
        }
        String key = "7d5c4e2d4aa475b9682aa7c15217e84b";
        String requestUrl = "https://restapi.amap.com/v3/config/district?keywords="+keyEncode+"&subdistrict="+subdistrict+"&key="+key;
        String requestMethod = "GET";
        Object lock = new Object();

        List<AmapApiDistrictJsonRO> amapApiDistrictJsonROS = amapService.downloadAmapDistrict(requestUrl, requestMethod,Long.valueOf(0));
        //存放新增地区对象
        List<AmapApiDistrictJsonRO> newAmapApiDistrictJsonROS = new ArrayList<AmapApiDistrictJsonRO>();
        //街道上一级
        String district = null;
        String city = null;
        //如果是街道一级的对象，调用 地理/逆地理编码 接口反查街道一级adcode
        if(!amapApiDistrictJsonROS.isEmpty()){
            for (int i=amapApiDistrictJsonROS.size()-1; i>=0; i--){

                AmapApiDistrictJsonRO amapApiDistrictJsonRO = amapApiDistrictJsonROS.get(i);
                if (amapApiDistrictJsonRO.getLevel() != null){
                    //如果是市一级，加其他
                    if (amapApiDistrictJsonRO.getLevel().contains("city")){
                        setOtherDistrict(newAmapApiDistrictJsonROS, amapApiDistrictJsonRO);
                        city = amapApiDistrictJsonRO.getName();
                        district = null;
                    }
                    if (amapApiDistrictJsonRO.getLevel().contains("district")){
                        district = amapApiDistrictJsonRO.getName();
                    }
/*
                    //市级街道数据
                    if (amapApiDistrictJsonRO.getLevel().contains("street")){
                        getStreetTownCode(key, requestMethod, amapApiDistrictJsonRO,city,district);
                    }*/

                }else {
                    System.out.print(amapApiDistrictJsonRO);
                }
            }
        }

        amapApiDistrictJsonROS.addAll(newAmapApiDistrictJsonROS);
        amapApiDistrictJsonSum.addAll(amapApiDistrictJsonROS);

      /*  //生成Excel
        try {

            if (createExcel(response, amapApiDistrictJsonROS,keywords)) {return;}
        }catch (Exception e) {
            e.printStackTrace();
        }
        //生成文件流
        try {
            createText(amapApiDistrictJsonROS,keywords);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /**
     * @author hxz
     * @date  2019/2/20/020 14:10
     * @param subdistrict
     * @param key
     * @param requestMethod
     * @param onleStrreROs
     * @param amapApiDistrictJsonRO
     * 获取高德街道级数据
     */
    private void getAmapSteetInfo(String subdistrict, String key, String requestMethod, List<AmapApiDistrictJsonRO> onleStrreROs, AmapApiDistrictJsonRO amapApiDistrictJsonRO) throws UnsupportedEncodingException {
        String requestUrl;
        //存放街道级数据
        List<AmapApiDistrictJsonRO> streetROs;
        requestUrl = "https://restapi.amap.com/v3/config/district?keywords="+amapApiDistrictJsonRO.getAdcode()+"&subdistrict="+subdistrict+"&key="+key;
        streetROs = amapService.downloadAmapDistrict(requestUrl, requestMethod,Long.valueOf(0));
        if (!streetROs.isEmpty()){
            for (AmapApiDistrictJsonRO streetRO :streetROs){
                if (StringUtils.isNotEmpty(streetRO.getLevel())){
                    if (streetRO.getLevel().contains("street")){
//                        getStreetTownCode(key,requestMethod,streetRO);
                        onleStrreROs.add(streetRO);
                    }
                }
            }
        }
    }

    /**
     * @author hxz
     * @date  2019/2/20/020 11:29
     * @param newAmapApiDistrictJsonROS
     * @param amapApiDistrictJsonRO
     * 设置其他区
     */
    private void setOtherDistrict(List<AmapApiDistrictJsonRO> newAmapApiDistrictJsonROS, AmapApiDistrictJsonRO amapApiDistrictJsonRO) {
        AmapApiDistrictJsonRO newAmapApiDistrictJsonRO = new AmapApiDistrictJsonRO();
        newAmapApiDistrictJsonRO.setName("其它区");
        newAmapApiDistrictJsonRO.setAdcode(amapApiDistrictJsonRO.getAdcode().substring(0,4)+"99");
        newAmapApiDistrictJsonRO.setCitycode(amapApiDistrictJsonRO.getCitycode());
        newAmapApiDistrictJsonRO.setCenter(amapApiDistrictJsonRO.getCenter());
        newAmapApiDistrictJsonRO.setLevel("district");
        newAmapApiDistrictJsonROS.add(newAmapApiDistrictJsonRO);
    }

    /**
     * @author hxz
     * @date  2019/2/20/020 11:27
     * @param key
     * @param requestMethod
     * @param amapApiDistrictJsonRO
     * 获取街道的towncode
     */
    private void getStreetTownCode(String key, String requestMethod, AmapApiDistrictJsonRO amapApiDistrictJsonRO,String city,String district)
            throws UnsupportedEncodingException {
        String requestUrl;//根据市区，获取经纬度
        String name = URLEncoder.encode(amapApiDistrictJsonRO.getName(), "UTF-8");
        city = URLEncoder.encode(city, "UTF-8");
        //有的街道在市底下，不在区底下
        district = (district == null ? "" : URLEncoder.encode(district, "UTF-8"));

        System.out.print("街道twoncode"+amapApiDistrictJsonRO.getName()+amapApiDistrictJsonRO.getAdcode());

        requestUrl = "https://restapi.amap.com/v3/geocode/geo?address="+ city+district+name +"&key="+key;
        List<String> amapCoordsByNameAndAdcode = amapService.getAmapCoordsByNameAndAdcode(requestUrl, requestMethod);
        if (!amapCoordsByNameAndAdcode.isEmpty()){
            forAmapCoords(key, requestMethod, amapApiDistrictJsonRO, amapCoordsByNameAndAdcode);
        }

        //高德操蛋的查询，有时候单查名字查不到经纬度，需要拼接 adcode,例如石龙镇,还有多个查询只随机返回一个的
        if (amapCoordsByNameAndAdcode.isEmpty() || amapApiDistrictJsonRO.getDetailCode() == null){
            requestUrl = "https://restapi.amap.com/v3/geocode/geo?address="+ name +"&key="+key;
            List<String> amapCoords = amapService.getAmapCoords(requestUrl, requestMethod);
            //根据经纬度获取街道adcode
            if (!amapCoords.isEmpty()){
                forAmapCoords(key,requestMethod,amapApiDistrictJsonRO,amapCoords);
            }
        }
    }

    /**
     * @author hxz
     * @date  2019/2/20/020 14:02
     * @param key
     * @param requestMethod
     * @param amapApiDistrictJsonRO
     * @param amapCoordsByNameAndAdcode
     * 根据经纬度查询地区信息
     */
    private void forAmapCoords(String key, String requestMethod, AmapApiDistrictJsonRO amapApiDistrictJsonRO,
                               List<String> amapCoordsByNameAndAdcode) {
        String requestUrl;
        String towncode;
        if (!amapCoordsByNameAndAdcode.isEmpty()){
            for (String amapCoordByNameAndAdcode : amapCoordsByNameAndAdcode){
                requestUrl = "https://restapi.amap.com/v3/geocode/regeo?output=JSON&location="+amapCoordByNameAndAdcode+
                        "&key="+key+"&radius=100&extensions=base&batch=false&roadlevel=1";
                towncode = amapService.downloadStreetCode(requestUrl, requestMethod,amapApiDistrictJsonRO.getAdcode());
                amapApiDistrictJsonRO.setDetailCode(towncode);
                amapApiDistrictJsonRO.setAlterCoords(amapCoordByNameAndAdcode);
            }

        }
    }

    private boolean createExcel(HttpServletResponse response, List<AmapApiDistrictJsonRO> amapApiDistrictJsonROS,String keywords) throws IOException {
        long time = System.currentTimeMillis();
        File writename = new File("C:\\Users\\Administrator\\Desktop\\工作文档\\全国省市县区街道社区数据库\\20190505\\"+"amap_adcode_"+keywords+String.valueOf(time) +".xlsx");
        // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
        writename.createNewFile(); // 创建新文件
        OutputStream os = new FileOutputStream(writename);
//        OutputStream os = response.getOutputStream();
        //创建工作簿对象
        XSSFWorkbook wb=new XSSFWorkbook();
        String fileName = "amap_adcode_" +String.valueOf(time) +".xlsx";

        if(amapApiDistrictJsonROS.isEmpty()){
            os.write("数据为空".toString().getBytes());
            return true;
        }
        //<65535
        if(amapApiDistrictJsonROS.size() > 65535){
            os.write("导出数据过大".toString().getBytes());
            return true;
        }
        Map<String, Object> result =  ExcelUtils.createExcel(wb,fileName, lineTransportHeaders, amapApiDistrictJsonROS, lineTransportParams);
        if(!(Boolean) result.get("success")){
            os.write(result.get("message").toString().getBytes());
            return true;
        }
        //响应到客户端
        ResponseHeaderUtil.setResponseHeader(response, fileName);

        wb.write(os);
        os.flush();
        os.close();
        return false;
    }

    /**
     * 生成TXT文件流
     */
    private void createText(List<AmapApiDistrictJsonRO> amapApiDistrictJsonROS,String keywords) throws IOException {

        long time = System.currentTimeMillis();
        /* 写入Txt文件 */
        // 相对路径，如果没有则要建立一个新的output。txt文件
        File writename = new File("C:\\Users\\Administrator\\Desktop\\工作文档\\全国省市县区街道社区数据库\\20190505\\"+keywords+String.valueOf(time) +".txt");
        // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
        writename.createNewFile(); // 创建新文件
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        try {
            if (CollectionUtils.isNotEmpty(amapApiDistrictJsonROS)){
                for (AmapApiDistrictJsonRO amapApiDistrictJsonRO : amapApiDistrictJsonROS){
                    out.write(amapApiDistrictJsonRO.getCitycode()+ "\t");
                    out.write(amapApiDistrictJsonRO.getAdcode()+ "\t");
                    out.write(amapApiDistrictJsonRO.getName()+ "\t");
                    out.write(amapApiDistrictJsonRO.getLevel()+ "\t");
                    out.write(amapApiDistrictJsonRO.getDetailCode()+ "\t");
                    out.write(amapApiDistrictJsonRO.getCenter()+ "\t");
                    out.write(amapApiDistrictJsonRO.getAlterCoords()+ "\t\r");
                }
            }
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
            System.out.print("生成"+keywords+"文件流");
        }catch (Exception e){
            System.out.print(e);
        }
    }

}
