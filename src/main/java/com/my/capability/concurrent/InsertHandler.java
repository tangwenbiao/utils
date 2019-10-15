package com.my.capability.concurrent;

import com.my.capability.IBusinessService;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: TangFenQi
 * @description:
 * @date：2019/4/25 17:20
 */
public class InsertHandler extends AbstractConcurrentHandler {

  private IBusinessService businessService;

  public InsertHandler(CountDownLatch countDownLatch, Integer amount,
      AtomicInteger endPoint, AtomicInteger errCount,
      IBusinessService businessService) {
    super(endPoint, countDownLatch, amount, errCount);
    this.businessService = businessService;
  }

  @Override
  public void run() {
    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("start! thread id:" + Thread.currentThread().getId());
    for (int i = 0; i < amount; i++) {
      try {
        businessService.go();
      } catch (Exception ex) {
        errCount.addAndGet(1);
      }finally {
        endPoint.addAndGet(1);
      }
    }
    System.out.println("end! thread id:" + Thread.currentThread().getId());
  }

  public static void main(String[] args) {
    System.out.println(new BigDecimal("30").divide(new BigDecimal(100)));
  }
}
