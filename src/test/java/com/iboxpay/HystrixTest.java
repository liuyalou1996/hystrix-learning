package com.iboxpay;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.iboxpay.hystrix.command.HelloCommand;

public class HystrixTest {

  @Test
  public void executeCommandTest() {
    String url = "http://localhost:8080/hystrix-learning/helloWithError";
    HelloCommand command = new HelloCommand(url);
    String result = command.execute();
    assertNotNull(result);
    System.out.println("请求异常的服务执行结果为：" + result);
  }

}
