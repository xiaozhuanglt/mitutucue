package com.xiaozhuanglt.mitutucue.service.facadeimpl;

import com.xiaozhuanglt.mitutucue.facade.JsonTranslateFacade;

import java.util.List;
import java.util.Map;

/**
 * @description: json转换接口
 * @author: hxz
 * @create: 2019-06-16 13:07
 **/
public class JsonTranslateFacadeImpl implements JsonTranslateFacade {
  /**
   * @param inputJson
   * @param inputKeyWord
   * @return {@link List< Map< String, String>>}
   * @author hxz
   * @date 2019/6/16 13:06
   * @Description: 根据关键词抽取json中的value
   * @Param: [inputJson, inputKeyWord]
   */
  @Override
  public List<Map<String, String>> getValueByKey(String inputJson, String inputKeyWord) {
    return null;
  }

}
