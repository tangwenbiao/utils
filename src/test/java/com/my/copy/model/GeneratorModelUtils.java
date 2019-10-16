package com.my.copy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.util.Assert;

/**
 * @author: TangFenQi
 * @description: 生成实体
 * @date：2019/10/15 15:30
 */
public class GeneratorModelUtils {

  /**
   * 生成测试实体
   *
   * @param count 实体个数
   * @param detailCount 详情的个数
   * @return 生成的实体集合
   */
  public static List<CompareA> generator(Integer count, Integer detailCount) {
    Assert.notNull(count, "count is empty!");
    Assert.isTrue(count > 0, "count is less than or equal to 0!!");
    Assert.notNull(detailCount, "detail count is empty!");
    Assert.isTrue(detailCount >= 0, "detail count is less than or equal to 0!!!");
    List<CompareA> compareAList = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      compareAList.add(generator(detailCount));
    }
    return compareAList;
  }

  private static CompareA generator(int detailCount) {
    CompareA compareA = CompareA.builder()
        .address("abc")
        .age(12)
        .phone("123124124")
        .name("a13214")
        .schoolName("aa123")
        .cEnum(CompareAEnum.B)
        .detailList(generatorDetail(detailCount))
        .build();
    return compareA;
  }

  private static List<CompareADetail> generatorDetail(int detailCount) {
    List<CompareADetail> compareADetailList = new ArrayList<>();
    for (int i = 0; i < detailCount; i++) {
      compareADetailList.add(generatorDetail());
    }
    return compareADetailList;
  }

  private static CompareADetail generatorDetail() {
    CompareADetail detail = CompareADetail.builder()
        .area("123aa")
        .car("asdfa")
        .dog("a1231")
        .house("1233")
        .detailEnum(CompareADetailEnum.B)
        .build();
    return detail;
  }

}
