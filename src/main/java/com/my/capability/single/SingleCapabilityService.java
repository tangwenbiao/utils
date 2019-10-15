package com.my.capability.single;

import com.my.capability.CapabilityBaseService;
import com.my.capability.IBusinessService;

/**
 * @author: TangFenQi
 * @description: 单线程性能测试
 * @date：2019/10/15 16:03
 */
public class SingleCapabilityService {

  private CapabilityBaseService baseService = new CapabilityBaseService();

  public void capabilityTest(IBusinessService businessService) throws InterruptedException {
    baseService.simpleTrendTest(1,1,businessService);
  }

}
