package com.iboxpay.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class OpenCircuitBreakerCommand extends HystrixCommand<String> {

  public OpenCircuitBreakerCommand() {
    // 设置命令执行的超时时间为500毫秒
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500)));
  }

  @Override
  protected String run() throws Exception {
    // 命令执行总会超时
    Thread.sleep(800);
    return "";
  }

  @Override
  protected String getFallback() {
    return "";
  }

}
