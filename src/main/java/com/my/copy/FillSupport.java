package com.my.copy;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author: TangFenQi
 * @description: 属性填充支持
 * @date：2019/12/31 10:49
 */
public interface FillSupport {

  void fill(Map<String, Field> targetFieldMap,Object target,Object sources);

}
