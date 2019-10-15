package com.my.capability;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

/**
 * @author: TangFenQi
 * @description:
 * @date：2019/4/25 17:08
 */
@Service
public class PerformanceService {

  /**
   * 趋势测试
   * <p>
   * 小范围不自增
   *
   * @param amount 测试条数
   */
  public void simpleTrendTest(Integer amount, IBusinessService businessService)
      throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    Integer concurrenceAmount = Runtime.getRuntime().availableProcessors() * 2;
    Integer everyAmount = amount / concurrenceAmount;
    AtomicInteger endPoint = new AtomicInteger(0);
    AtomicInteger errPoint = new AtomicInteger(0);
    //放入线程
    for (int i = 0; i < concurrenceAmount; i++) {
      new InsertHandler(countDownLatch, everyAmount,
          endPoint, errPoint, businessService).start();
    }
    Thread.sleep(1000L);
    //执行
    Long startTime = System.currentTimeMillis();
    countDownLatch.countDown();
    //记录结果
    while (true) {
      if (amount.equals(endPoint.get())) {
        System.out.println("整体时间(单位毫秒):" + (System.currentTimeMillis() - startTime));
        System.out.println("异常的数量:" + errPoint.get() + " 总体数量:" + endPoint.get());
        break;
      }
    }
  }

  /**
   * 当已经插入大编号
   * <p>
   * 小范围不自增
   *
   * @param amount 测试条数
   * @param previousAmount 大编号测试条数
   */
  public void exceptionTrendTest(Integer amount, Integer previousAmount) {
    //插入大编号

    //放入线程

    //执行

    //记录结果
  }


}
