package com.my.capability;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: TangFenQi
 * @description: 并发流程处理基类
 * @date：2019/10/8 10:19
 */
public abstract class AbstractConcurrentHandler extends Thread {

  /**
   * 结束点
   */
  protected AtomicInteger endPoint;

  /**
   * 错误计数
   */
  protected AtomicInteger errCount;

  /**
   * 并发信号枪
   */
  protected CountDownLatch countDownLatch;

  /**
   * 执行次数
   */
  protected Integer amount;

  public AbstractConcurrentHandler(AtomicInteger endPoint,
      CountDownLatch countDownLatch, Integer amount, AtomicInteger errCount) {
    this.endPoint = endPoint;
    this.countDownLatch = countDownLatch;
    this.amount = amount;
    this.errCount = errCount;
  }
}
