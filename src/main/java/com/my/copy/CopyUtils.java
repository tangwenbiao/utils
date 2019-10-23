package com.my.copy;

import com.google.gson.Gson;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.naming.Name;
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

  private static Map<String, BeanCopier> beanCopierMap;

  private static Map<Class, Field[]> fieldCacheMap;

  private static Map<Class, Enum[]> enumsCacheMap;

  private static Map<Class, Method> valueOfMethodCache;

  private static Map<String, Enum> targetEnumOfName;

  static {
    beanCopierMap = new HashMap<>();
    fieldCacheMap = new HashMap<>();
    enumsCacheMap = new HashMap<>();
    targetEnumOfName = new HashMap<>();
    valueOfMethodCache=new HashMap<>();
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
    List<Field> listFields = getListTypeFromFields(sourceClazz);
    //填充list
    fillList(listFields, targetFieldMap, sources, target);
    //查询是否有枚举
    List<Field> enumFields = getEnumTypeFromFields(sourceClazz);
    //填充enum
    fillEnum(enumFields, targetFieldMap, sources, target);
  }

  private static void fillEnum(List<Field> fieldList, Map<String, Field> targetFieldMap,
      Object source, Object target) {
    for (Field field : fieldList) {
      //根据属性名匹配
      String fieldName = field.getName();
      Field targetField = targetFieldMap.get(fieldName);
      if (targetField != null) {
        //获取原始值
        Enum sourceEnum = (Enum) getInstance(source, field);
        //获取枚举值
        Object enumValue = getEnumByName(targetField, target, sourceEnum.name());
        //设置枚举值
        setField(target, targetField, enumValue);
      }
    }

  }

  private static Object getEnumByName(Field targetField, Object target, String name) {
    //判断是否有该枚举值
    Object enumValue;
    if (existEnumName(targetField, target, name)) {
      //获取valueOf 方法
      Method targetValueOfMethod = getEnumMethodAboutValueOf(targetField);
      //获取枚举值
      enumValue = getEnumValueByValueOf(targetValueOfMethod, target, name);
    } else {
      enumValue = null;
    }
    return enumValue;
  }

  private static boolean existEnumName(Field targetField, Object target, String name) {
    //获取所有枚举值
    Enum[] enumList = getEnumList(targetField, target);
    //判断name是否存在
    for (Enum e : enumList) {
      if (e.name().equals(name)) {
        return true;
      }
    }
    return false;
  }

  private static Enum[] getEnumList(Field targetField, Object target) {
    Enum[] enumList;
    if (enumsCacheMap.containsKey(target.getClass())) {
      return enumsCacheMap.get(target.getClass());
    }
    //获取values方法
    Method targetValuesOfMethod = getEnumMethodAboutValues(targetField);
    try {
      enumList = (Enum[]) targetValuesOfMethod.invoke(target);
      enumsCacheMap.put(target.getClass(), enumList);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("not found values method!! class:{} err:{}", target.getClass(), e);
      throw new RuntimeException("copier of the failure!");
    }
    return enumList;
  }

  private static Object getEnumValueByValueOf(Method targetValueOfMethod, Object target,
      String name) {
    String key = getEnumKeyByName(target, name);
    if (targetEnumOfName.containsKey(key)) {
      return targetEnumOfName.get(key);
    }
    Object enumValue;
    try {
      enumValue = targetValueOfMethod.invoke(target, name);
      targetEnumOfName.put(key, (Enum) enumValue);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("not found value of method!! class:{} err:{}", target.getClass(), e);
      throw new RuntimeException("copier of the failure!");
    }
    return enumValue;
  }

  private static String getEnumKeyByName(Object target, String name) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(target.getClass());
    stringBuilder.append("&");
    stringBuilder.append(name);
    return stringBuilder.toString();
  }

  private static Method getEnumMethodAboutValueOf(Field targetField) {
    Method targetValueOfMethod;
    if (valueOfMethodCache.containsKey(targetField.getType())) {
      return valueOfMethodCache.get(targetField.getType());
    }
    try {
      targetValueOfMethod = targetField.getType().getMethod("valueOf", String.class);
      valueOfMethodCache.put(targetField.getType(), targetValueOfMethod);
    } catch (NoSuchMethodException e) {
      log.error("not found value of method!! class:{}", targetField.getClass());
      throw new RuntimeException("copier of the failure!");
    }
    return targetValueOfMethod;
  }

  private static Method getEnumMethodAboutValues(Field targetField) {
    Method targetValueOfMethod;
    try {
      targetValueOfMethod = targetField.getType().getMethod("values");
    } catch (NoSuchMethodException e) {
      log.error("not found value of method!! class:{}", targetField.getClass());
      throw new RuntimeException("copier of the failure!");
    }
    return targetValueOfMethod;
  }

  private static void fillList(List<Field> fields, Map<String, Field> targetFieldMap,
      Object sources, Object target) {
    for (Field field : fields) {
      //根据属性名匹配
      String fieldName = field.getName();
      Field targetField = targetFieldMap.get(fieldName);
      if (targetField != null) {
        //找到sources 的list 对象实例
        List sourceList = (List) getInstance(sources, field);
        ;
        if (CollectionUtils.isEmpty(sourceList)) {
          sourceList = new ArrayList();
          setField(target, targetField, new ArrayList());
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
        setField(target, targetField, targetList);
      }
    }
  }

  private static void clearList(Object object, Map<String, Field> targetFieldMap) {
    targetFieldMap.forEach((k, v) -> {
      if (v.getType().equals(List.class)) {
        setField(object, v, null);
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

  private static Object getInstance(Object sources, Field field) {
    Object sourcesListInstance;
    try {
      field.setAccessible(true);
      sourcesListInstance = field.get(sources);
    } catch (IllegalAccessException e) {
      log.error("not found list!!", e);
      throw new RuntimeException("copier of the failure!");
    }
    return sourcesListInstance;
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

  private static void setField(Object target, Field targetField, Object object) {
    try {
      targetField.setAccessible(true);
      targetField.set(target, object);
    } catch (IllegalAccessException e) {
      log.error("set list is err! target:{},field Name:{},list:{}", gson.toJson(target),
          targetField.getName(), gson.toJson(object), e);
      throw new RuntimeException("copier of the failure!");
    }
  }


  private static Class getClassByList(Field field) {
    //此处借助了spring框架提供的类 泛型类型
    ResolvableType resolvableType = ResolvableType.forField(field);
    return resolvableType.getGeneric(0).resolve();
  }

  private static List<Field> getListTypeFromFields(Class clazz) {
    return getFieldsByType(clazz, List.class);
  }

  private static List<Field> getEnumTypeFromFields(Class clazz) {
    Field[] fields = getField(clazz);
    List<Field> fieldList = Arrays.stream(fields)
        .filter(field -> field.getType().isEnum())
        .collect(Collectors.toList());
    return fieldList;
  }

  private static List<Field> getFieldsByType(Class clazz, Class typeClass) {
    List<Field> fieldList = Arrays.stream(getField(clazz))
        .filter(field -> field.getType().equals(typeClass))
        .collect(Collectors.toList());
    return fieldList;
  }

  private static Field[] getField(Class targetClazz) {
    Field[] fields;
    if (fieldCacheMap.containsKey(targetClazz)) {
      fields = fieldCacheMap.get(targetClazz);
    } else {
      fields = targetClazz.getDeclaredFields();
    }
    return fields;
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
