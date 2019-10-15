package com.my.copy.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: TangFenQi
 * @description: 用于比较的A类
 * @date：2019/10/15 15:26
 */
@Data
@Builder
public class CompareA {

  private String name;

  private Integer age;

  private String address;

  private String schoolName;

  private String phone;

  private List<CompareADetail> detailList;

}
