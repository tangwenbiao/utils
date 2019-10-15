package com.my.copy;

import com.my.copy.model.CompareA;
import com.my.copy.model.CompareADetail;
import com.my.copy.model.CompareAEnum;
import com.my.copy.model.CompareB;
import com.my.copy.model.GeneratorModelUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: TangFenQi
 * @description: 拷贝工具测试类
 * @date：2019/10/15 15:05
 */
@SpringBootApplication
@RunWith(SpringJUnit4ClassRunner.class)
public class CopyUtilsTest {


  @Test
  public void copyTest() {
    List<CompareA> compareAList = GeneratorModelUtils.generator(1, 1);
    CompareA compareA = compareAList.get(0);
    CompareB compareB = new CompareB();
    CopyUtils.copy(compareA, compareB);
    Assert.assertEquals(compareA.getAge(), compareB.getAge());
    Assert.assertEquals(compareA.getAddress(), compareB.getAddress());
    Assert.assertEquals(compareA.getName(), compareB.getName());
    Assert.assertEquals(compareA.getPhone(), compareB.getPhone());
    Assert.assertEquals(compareA.getSchoolName(), compareB.getSchoolName());

    System.out.println();
  }

  @Test
  public void copyUtils100000() {
    //初始化实体
    int count = 100000;
    int detail = 0;
    List<CompareA> compareAList = GeneratorModelUtils.generator(count, detail);
    //进行测试
    List<CompareB> compareBList = new ArrayList<>(count);
    //测试
    Long startTime = System.currentTimeMillis();
    for (CompareA compareA : compareAList) {
      CompareB compareB = new CompareB();
      CopyUtils.copy(compareA, compareB);
      compareBList.add(compareB);
    }
    Long endTime = System.currentTimeMillis();
    System.out
        .println("耗时:" + (endTime - startTime) / 1000 + "秒," + (endTime - startTime) % 1000 + "毫秒");
    System.out.println();
  }

  @Test
  public void compareWithBeanCopier100000() {
    //初始化实体
    int count = 100000;
    int detail = 0;
    List<CompareA> compareAList = GeneratorModelUtils.generator(count, detail);
    //获取比较类
    BeanCopier beanCopier = BeanCopier.create(CompareA.class, CompareB.class, false);
    //进行测试
    List<CompareB> compareBList = new ArrayList<>(count);
    //测试
    Long startTime = System.currentTimeMillis();
    for (CompareA compareA : compareAList) {
      CompareB compareB = new CompareB();
      beanCopier.copy(compareA, compareB, null);
      compareBList.add(compareB);
    }
    Long endTime = System.currentTimeMillis();
    System.out
        .println("耗时:" + (endTime - startTime) / 1000 + "秒," + (endTime - startTime) % 1000 + "毫秒");
    System.out.println();
  }

  @Test
  public void CompareWithBeanUtilsInLang100000() {
    //初始化实体
    int count = 100000;
    int detail = 0;
    List<CompareA> compareAList = GeneratorModelUtils.generator(count, detail);
    //进行测试
    List<CompareB> compareBList = new ArrayList<>(count);
    //测试
    Long startTime = System.currentTimeMillis();
    for (CompareA compareA : compareAList) {
      CompareB compareB = new CompareB();
      BeanUtils.copyProperties(compareA, compareB);
      compareBList.add(compareB);
    }
    Long endTime = System.currentTimeMillis();
    System.out
        .println("耗时:" + (endTime - startTime) / 1000 + "秒," + (endTime - startTime) % 1000 + "毫秒");
    System.out.println();
  }

  @Test
  public void CompareWithBeanUtilsInSpring100000()
      throws InvocationTargetException, IllegalAccessException {
    //初始化实体
    int count = 100000;
    int detail = 0;
    List<CompareA> compareAList = GeneratorModelUtils.generator(count, detail);
    //进行测试
    List<CompareB> compareBList = new ArrayList<>(count);
    //测试
    Long startTime = System.currentTimeMillis();
    for (CompareA compareA : compareAList) {
      CompareB compareB = new CompareB();
      org.apache.commons.beanutils.BeanUtils.copyProperties(compareA, compareB);
      compareBList.add(compareB);
    }
    Long endTime = System.currentTimeMillis();
    System.out
        .println("耗时:" + (endTime - startTime) / 1000 + "秒," + (endTime - startTime) % 1000 + "毫秒");
    System.out.println();
  }

  @Test
  public void test() throws NoSuchMethodException {
    Method method = CompareAEnum.A.getClass().getMethod("valueOf", String.class);
    System.out.println();
  }

}
