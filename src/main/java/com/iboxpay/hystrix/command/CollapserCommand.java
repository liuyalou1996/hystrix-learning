package com.iboxpay.hystrix.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.iboxpay.bean.Person;
import com.netflix.hystrix.HystrixCollapser.CollapsedRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CollapserCommand extends HystrixCommand<Map<String, Person>> {

  // 请求集合，第一个参数是单个请求返回的数据类型，第二个参数是请求参数的类型
  private Collection<CollapsedRequest<Person, String>> requests;

  public CollapserCommand(Collection<CollapsedRequest<Person, String>> requests) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")));
    this.requests = requests;
  }

  @Override
  protected Map<String, Person> run() throws Exception {
    System.out.println("收集参数后执行命令，参数数量：" + requests.size());
    List<String> nameList = new ArrayList<String>();
    for (CollapsedRequest<Person, String> request : requests) {
      nameList.add(request.getArgument());
    }

    // 这里模拟服务调用
    Map<String, Person> resultMap = new HashMap<>();
    nameList.forEach(name -> {
      Person p = new Person();
      p.setId(UUID.randomUUID().toString());
      p.setName(name);
      p.setAge(new Random().nextInt(30));
      resultMap.put(name, p);;
    });

    return resultMap;
  }

  @Override
  protected Map<String, Person> getFallback() {
    return Collections.emptyMap();
  }

}
