package com.my.copy.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: TangFenQi
 * @description: 用于比较的A类详情
 * @date：2019/10/15 15:26
 */
@Data
@Builder
public class CompareADetail {

  private String area;

  private String car;

  private String dog;

  private String house;

  private CompareADetailEnum detailEnum;

}
