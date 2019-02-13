package com.iboxpay;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.configuration.AbstractConfiguration;
import org.junit.Test;

import com.iboxpay.hystrix.command.HelloCommand;
import com.iboxpay.hystrix.command.OpenCircuitBreakerCommand;
import com.iboxpay.hystrix.command.TriggerIsolationCommand;
import com.netflix.config.ConfigurationManager;

public class HystrixTest {

  @Test
  public void executeCommandTest() {
    String url = "http://localhost:8080/hystrix-learning/helloWithError";
    // 默认情况下如果超过1秒未响应则会调用回退方法
    HelloCommand command = new HelloCommand(url);
    String result = command.execute();
    assertNotNull(result);
    System.out.println("请求异常的服务执行结果为：" + result);
  }

  @Test
  public void openCircuitBreakerTest() {
    AbstractConfiguration config = ConfigurationManager.getConfigInstance();
    // 时间阀值
    config.setProperty("hystrix.command.default.metrics.rollingStats.timeInMillseconds", 10000);
    // 请求阀值
    config.setProperty("hystrix.command.default.circuitBreaker.requestVolumeThreshold", 10);
    // 请求错误率
    config.setProperty("hystrix.command.default.circuitBreaker.errorThresholdPercentage", 50);
    for (int count = 0; count < 15; count++) {
      // 执行的命令都会超时
      OpenCircuitBreakerCommand command = new OpenCircuitBreakerCommand();
      command.execute();
      if (command.isCircuitBreakerOpen()) {
        System.err.println("断路器被打开，第" + (count + 1) + "个命令不会再被执行!");
      }
    }
  }

  @Test
  public void threadIsolationTest() throws Exception {
    AbstractConfiguration config = ConfigurationManager.getConfigInstance();
    config.setProperty("hystrix.threadpool.default.coreSize", 3);
    for (int i = 0; i < 6; i++) {
      TriggerIsolationCommand command = new TriggerIsolationCommand(i);
      command.queue();
    }

    Thread.sleep(5000);
  }
}
