package com.my.capability.concurrent;

import com.my.capability.CapabilityBaseService;
import com.my.capability.IBusinessService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

/**
 * @author: TangFenQi
 * @description:
 * @date：2019/4/25 17:08
 */
public class ConcurrentCapabilityService {

  private CapabilityBaseService baseService = new CapabilityBaseService();

  /**
   * 趋势测试
   * <p>
   * 小范围不自增
   *
   * @param amount 测试条数
   */
  public void simpleTrendTest(Integer amount, IBusinessService businessService)
      throws InterruptedException {
    //设置线程数
    Integer concurrenceAmount = Runtime.getRuntime().availableProcessors() * 2;
    //每个线程需要执行的次数
    Integer everyAmount = amount / concurrenceAmount;
    baseService.simpleTrendTest(concurrenceAmount, everyAmount, businessService);
  }

}
