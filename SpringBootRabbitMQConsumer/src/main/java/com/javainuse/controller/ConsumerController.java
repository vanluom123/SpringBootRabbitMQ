package com.javainuse.controller;

import com.javainuse.model.Employee;
import com.javainuse.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/javainuse-rabbitmq")
@RestController
public class ConsumerController {
  @Autowired
  private MessageUtil messageUtil;

  @GetMapping("/employee")
  public Employee getEmployee() throws IOException {
    return messageUtil.consume("javainuse.queue", Employee.class);
  }
}
