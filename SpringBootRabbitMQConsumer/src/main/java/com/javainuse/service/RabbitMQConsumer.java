//package com.javainuse.service;
//
//import com.javainuse.exception.InvalidSalaryException;
//import com.javainuse.model.Employee;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//import static com.javainuse.util.MessageUtil.convertMessageToObject;
//
//@Component
//public class RabbitMQConsumer {
//	private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
//	private Employee employee;
//
//	public Employee getEmployee() {
//		return employee;
//	}
//
//	@RabbitListener(queues = "${javainuse.rabbitmq.queue}")
//	public void receivedMessage(byte[] msg) {
//		try {
//			employee = convertMessageToObject(msg, Employee.class);
//			logger.info("Received Message From RabbitMQ: " + employee);
//			if (employee.getSalary() < 0) {
//				throw new InvalidSalaryException("salary is less than zero");
//			}
//		} catch (InvalidSalaryException | IOException e) {
//			logger.error(e.getMessage());
//		}
//	}
//}