package com.javainuse.controller;

import com.javainuse.model.Employee;
import com.javainuse.service.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/javainuse-rabbitmq/")
public class RabbitMQWebController {
  private final RabbitMQSender rabbitMQSender;

  @Autowired
  public RabbitMQWebController(RabbitMQSender rabbitMQSender) {
    this.rabbitMQSender = rabbitMQSender;
  }

  @GetMapping(value = "/producer")
  public String producer(@RequestParam("empName") String empName,
                         @RequestParam("empId") String empId,
                         @RequestParam("salary") int salary) {

    Employee emp = new Employee();
    emp.setEmpId(empId);
    emp.setEmpName(empName);
    emp.setSalary(salary);
    rabbitMQSender.send(emp);

    return "Message sent to the RabbitMQ JavaInUse Successfully";
  }
}
