package com.iboxpay;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Future;

import org.apache.commons.configuration.AbstractConfiguration;
import org.junit.Test;

import com.iboxpay.bean.Person;
import com.iboxpay.hystrix.collapser.MyHystrixCollapser;
import com.iboxpay.hystrix.command.HelloCommand;
import com.iboxpay.hystrix.command.OpenCircuitBreakerCommand;
import com.iboxpay.hystrix.command.TriggerIsolationCommand;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

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

  /**
   * 10秒内有10个请求进来，且10个请求的失败率达到50%，那么断路器就会打开
   */
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
    // 设置线程池核心线程数量为3
    config.setProperty("hystrix.threadpool.default.coreSize", 3);
    for (int i = 0; i < 6; i++) {
      TriggerIsolationCommand command = new TriggerIsolationCommand(i);
      command.queue();
    }

    Thread.sleep(5000);
  }

  @Test
  public void semaphoreIsolationTest() throws Exception {
    AbstractConfiguration config = ConfigurationManager.getConfigInstance();
    // 设置隔离策略为信号量隔离策略
    config.setProperty("hystrix.command.default.execution.isolation.strategy", ExecutionIsolationStrategy.SEMAPHORE);
    // 设置最大并发数，默认值为10，这里为2
    config.setProperty("hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests", 2);
    // 设置执行回退方法的最大并发，默认值为10，这里为20
    config.setProperty("hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests", 20);
    for (int i = 0; i < 6; i++) {
      int index = i;
      Thread thread = new Thread(() -> {
        TriggerIsolationCommand command = new TriggerIsolationCommand(index);
        command.execute();
      });
      thread.start();
    }

    Thread.sleep(5000);
  }

  /**
   * 对规定时间内同类型的请求(url相同，参数不同)进行请求的合并，多个请求在同一个命令中执行，节省了线程开销和网络连接
   * @throws Exception
   */
  @Test
  public void collapseRequestTest() throws Exception {
    AbstractConfiguration config = ConfigurationManager.getConfigInstance();
    // 收集1秒内发生的请求
    config.setProperty("hystrix.collapser.default.timerDelayInMilliseconds", "1000");
    // 请求上下文
    HystrixRequestContext context = HystrixRequestContext.initializeContext();
    // 创建请求合并处理器
    MyHystrixCollapser c1 = new MyHystrixCollapser("tom");
    MyHystrixCollapser c2 = new MyHystrixCollapser("jeny");
    MyHystrixCollapser c3 = new MyHystrixCollapser("john");
    MyHystrixCollapser c4 = new MyHystrixCollapser("nick");

    // 异步执行
    Future<Person> f1 = c1.queue();
    Future<Person> f2 = c2.queue();
    Future<Person> f3 = c3.queue();
    Future<Person> f4 = c4.queue();
    System.err.println(f1.get());
    System.err.println(f2.get());
    System.err.println(f3.get());
    System.err.println(f4.get());

    context.shutdown();
  }
}
