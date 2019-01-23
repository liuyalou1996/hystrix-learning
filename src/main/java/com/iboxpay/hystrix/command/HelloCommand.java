package com.iboxpay.hystrix.command;

import com.iboxpay.util.HttpClientUtils;
import com.iboxpay.util.HttpClientUtils.HttpClientResp;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class HelloCommand extends HystrixCommand<String> {

  private String url;

  public HelloCommand(String url) {
    super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
    this.url = url;
  }

  @Override
  protected String run() throws Exception {
    HttpClientResp resp = HttpClientUtils.sendGet(url, null, null);
    return resp.getRespStr();
  }

  /**
   * 默认情况下，如果调用的服务不能在1秒内完成，将会触发回退
   */
  @Override
  protected String getFallback() {
    return "调用超时!";
  }

}
