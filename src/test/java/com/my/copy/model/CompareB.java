package com.my.copy.model;

import java.util.List;
import lombok.Data;

/**
 * @author: TangFenQi
 * @description: 用于比较的B类详情
 * @date：2019/10/15 15:27
 */
@Data
public class CompareB {

  private CompareBEnum cEnum;

  private String name;

  private Integer age;

  private String address;

  private String schoolName;

  private String phone;

  private List<CompareBDetail> detailList;
}
