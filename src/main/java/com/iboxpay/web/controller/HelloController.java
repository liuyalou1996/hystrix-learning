package com.iboxpay.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @RequestMapping("/hello")
  public String sayHello() {
    return "Hellow World!";
  }

  @RequestMapping("/helloWithError")
  public String sayHelloWithError() throws InterruptedException {
    Thread.sleep(10000);
    return "Hello World With Error!";
  }
}
