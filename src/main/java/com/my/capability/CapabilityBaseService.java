package com.my.capability;

import com.my.capability.concurrent.InsertHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: TangFenQi
 * @description: 性能测试基础类
 * @date：2019/10/15 15:58
 */
public class CapabilityBaseService {

  /**
   * 趋势测试
   * <p>
   * 小范围不自增
   *
   * @param concurrenceAmount 并发数
   * @param everyAmount 每个线程执行数
   * @param businessService 需要测试的方法
   */
  public void simpleTrendTest(Integer concurrenceAmount, Integer everyAmount,
      IBusinessService businessService)
      throws InterruptedException {
    //每个线程需要执行的次数
    AtomicInteger endPoint = new AtomicInteger(0);
    AtomicInteger errPoint = new AtomicInteger(0);
    //放入线程
    CountDownLatch countDownLatch = new CountDownLatch(1);
    for (int i = 0; i < concurrenceAmount; i++) {
      new InsertHandler(countDownLatch, everyAmount,
          endPoint, errPoint, businessService).start();
    }
    Thread.sleep(1000L);
    //执行
    Long startTime = System.currentTimeMillis();
    countDownLatch.countDown();
    //记录结果
    int amount = everyAmount * concurrenceAmount;
    while (true) {
      if (amount == endPoint.get()) {
        System.out.println("整体时间(单位毫秒):" + (System.currentTimeMillis() - startTime));
        System.out.println("异常的数量:" + errPoint.get() + " 总体数量:" + endPoint.get());
        break;
      }
    }
  }
}
