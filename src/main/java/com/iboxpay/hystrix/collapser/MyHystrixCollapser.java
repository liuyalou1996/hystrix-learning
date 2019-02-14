package com.iboxpay.hystrix.collapser;

import java.util.Collection;
import java.util.Map;

import com.iboxpay.bean.Person;
import com.iboxpay.hystrix.command.CollapserCommand;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;

public class MyHystrixCollapser extends HystrixCollapser<Map<String, Person>, Person, String> {

  private String personName;

  public MyHystrixCollapser(String personName) {
    this.personName = personName;
  }

  @Override
  public String getRequestArgument() {
    return personName;
  }

  @Override
  protected HystrixCommand<Map<String, Person>> createCommand(Collection<CollapsedRequest<Person, String>> requests) {
    return new CollapserCommand(requests);
  }

  @Override
  protected void mapResponseToRequests(Map<String, Person> batchResponse,
      Collection<CollapsedRequest<Person, String>> requests) {
    // 结果与请求相关联
    requests.forEach(request -> {
      Person p = batchResponse.get(request.getArgument());
      request.setResponse(p);
    });
  }

}
