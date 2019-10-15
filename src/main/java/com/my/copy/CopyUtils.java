package com.my.copy;

import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author: TangFenQi
 * @description:
 * @date：2019/5/22 15:17
 */
@Slf4j
public class CopyUtils {

  private static Gson gson = new Gson();

  public static Map<String, BeanCopier> beanCopierMap;

  static {
    beanCopierMap = new HashMap<>();
  }

  public static void copy(Object sources, Object target) {
    Assert.notNull(sources, "source is empty!");
    //Assert.notNull(target, "target is empty!");
    //获取类型
    Class sourceClazz = sources.getClass();
    Class targetClazz = target.getClass();
    //获取目标类
    Map<String, Field> targetFieldMap = Arrays.stream(targetClazz.getDeclaredFields())
        .collect(Collectors.toMap(Field::getName, v -> v));
    //拷贝
    copyObject(sources, target);
    //clear list
    clearList(target, targetFieldMap);
    //查询是否有list
    List<Field> fields = getListTypeInFields(sourceClazz);
    for (Field field : fields) {
      //根据属性名匹配
      String fieldName = field.getName();
      Field targetField = targetFieldMap.get(fieldName);
      if (!(targetField == null)) {
        //找到sources 的list 对象实例
        List sourceList = getListInstance(sources, field);
        if (CollectionUtils.isEmpty(sourceList)) {
          sourceList = new ArrayList();
          setList(target, targetField, new ArrayList());
        }
        //找到target 中list的类型
        Class targetListClass = getClassByList(targetField);
        //实例化类型
        List targetList = new ArrayList();
        sourceList.forEach(c -> {
          Object targetObjectInList = instance(targetListClass);
          if (isBaseType(targetListClass)) {
            targetObjectInList = c;
          } else {
            copy(c, targetObjectInList);
          }
          targetList.add(targetObjectInList);
        });
        //set
        setList(target, targetField, targetList);
      }
    }
  }

  private static void clearList(Object object, Map<String, Field> targetFieldMap) {
    targetFieldMap.forEach((k, v) -> {
      if (v.getType().equals(List.class)) {
        setList(object, v, null);
      }
    });
  }

  /**
   * page实例类转换
   *
   * @param <T> 源对象
   * @param <K> 目标对象
   * @param source 源对象
   * @param target 目标对象
   */
  public static <T, K> List<K> copyList(List<T> source, Class<K> target) {
    List<K> list = new ArrayList<K>();
    for (T af : source) {

      BeanCopier beanCopier = BeanCopier.create(af.getClass(), target, false);
      K af1 = null;
      try {
        af1 = (K) target.newInstance();
      } catch (Exception e) {
        log.error("实例转换出错");
      }
      beanCopier.copy(af, af1, null);
      list.add(af1);
    }
    return list;
  }

  private static List getListInstance(Object sources, Field field) {
    Object sourcesListInstance;
    try {
      field.setAccessible(true);
      sourcesListInstance = field.get(sources);
    } catch (IllegalAccessException e) {
      log.error("not found list!!", e);
      throw new RuntimeException("copier of the failure!");
    }
    return (List) sourcesListInstance;
  }

  private static Object instance(Class clazz) {
    Object object;
    try {
      object = clazz.getConstructor().newInstance();
    } catch (InstantiationException e) {
      throw new RuntimeException("copier of the failure!");
    } catch (IllegalAccessException e) {
      throw new RuntimeException("copier of the failure!");
    } catch (InvocationTargetException e) {
      throw new RuntimeException("copier of the failure!");
    } catch (NoSuchMethodException e) {
      log.error("not found no argument constructor!");
      throw new RuntimeException("copier of the failure!");
    }
    return object;
  }

  private static void setList(Object target, Field targetField, List list) {
    try {
      targetField.setAccessible(true);
      targetField.set(target, list);
    } catch (IllegalAccessException e) {
      log.error("set list is err! target:{},field Name:{},list:{}", gson.toJson(target),
          targetField.getName(), gson.toJson(list), e);
      throw new RuntimeException("copier of the failure!");
    }
  }

  private static Class getClassByList(Field field) {
    //此处借助了spring框架提供的类 泛型类型
    ResolvableType resolvableType = ResolvableType.forField(field);
    return resolvableType.getGeneric(0).resolve();
  }

  private static List<Field> getListTypeInFields(Class clazz) {
    Field[] fields = clazz.getDeclaredFields();
    List<Field> fieldList = Arrays.stream(fields)
        .filter(field -> field.getType().equals(List.class))
        .collect(Collectors.toList());
    return fieldList;
  }

  private static Object copyObject(Object sources, Object target) {
    BeanCopier beanCopier = get(sources.getClass(), target.getClass());
    beanCopier.copy(sources, target, null);
    return target;
  }

  private static BeanCopier get(Class sources, Class target) {
    String key = getKey(sources, target);
    BeanCopier beanCopier;
    if ((beanCopier = beanCopierMap.get(key)) == null) {
      beanCopier = BeanCopier.create(sources, target, false);
      beanCopierMap.put(key, beanCopier);
    }
    return beanCopier;
  }

  private static String getKey(Class sources, Class target) {
    return sources.toString() + "|" + target.toString();
  }

  //是否是基础类型
  private static boolean isBaseType(Class clazz) {
    if (clazz.isPrimitive() || clazz.equals(String.class)) {
      return true;
    }
    return false;
  }


}
