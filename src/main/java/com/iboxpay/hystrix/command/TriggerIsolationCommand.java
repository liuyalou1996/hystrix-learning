package com.iboxpay.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class TriggerIsolationCommand extends HystrixCommand<String> {

  private int index;

  public TriggerIsolationCommand(int index) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")));
    this.index = index;
  }

  @Override
  protected String run() throws Exception {
    // 命令等待500毫秒后执行
    Thread.sleep(500);
    System.err.println("执行方法，当前索引：" + index);
    return "";
  }

  @Override
  protected String getFallback() {
    System.err.println("执行fallback，当前索引：" + index);
    return "";
  }

}
