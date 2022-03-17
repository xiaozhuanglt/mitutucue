package com.xiaozhuanglt.mitutucue.facade;

import java.util.List;
import java.util.Map;

/**
 * @author hxz
 * @date  2019/6/16 13:05
 * @Description: json转换接口
 */
public interface JsonTranslateFacade {

  /**
   * @author hxz
   * @date  2019/6/16 13:06
   * @Description: 根据关键词抽取json中的value
   * @Param: [inputJson, inputKeyWord]
   * @return {@link List< Map< String, String>>}
   */
  List<Map<String,String>> getValueByKey(String inputJson, String inputKeyWord);
}
