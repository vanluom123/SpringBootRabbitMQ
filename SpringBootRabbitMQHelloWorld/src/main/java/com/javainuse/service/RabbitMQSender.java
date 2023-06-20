package com.javainuse.service;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class RabbitMQSender {
  @Value("${javainuse.rabbitmq.exchange}")
  private String exchange;

  @Value("${javainuse.rabbitmq.routingkey}")
  private String routingKey;

  @Value("${javainuse.rabbitmq.queue}")
  private String queueName;

  private final AmqpTemplate amqpTemplate;

  private final Channel channel;

  @Autowired
  public RabbitMQSender(AmqpTemplate amqpTemplate, Channel channel) {
    this.amqpTemplate = amqpTemplate;
    this.channel = channel;
  }

  public <T> void send(T message) {
    amqpTemplate.convertAndSend(exchange, routingKey, message);
    log.info("Send msg = " + message);
  }

  public void send2(String message,
                    String exchange,
                    String queueName,
                    String routingKey) throws IOException {
    channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
    channel.queueDeclare(queueName, true, false, false, null);
    channel.queueBind(queueName, exchange, routingKey, null);
    channel.basicPublish(exchange, routingKey, null, message.getBytes());
  }

  public void send3(String message,
                    String exchange,
                    String queueName) throws IOException {
    send2(message, exchange, queueName, "");
  }
}