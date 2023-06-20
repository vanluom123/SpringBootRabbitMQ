package com.javainuse.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class RabbitMQConfig {

  @Value("${javainuse.rabbitmq.queue}")
  String queueName;
  @Value("${javainuse.rabbitmq.username}")
  private String userName;
  @Value("${javainuse.rabbitmq.password}")
  private String password;
  @Value("${javainuse.rabbitmq.uri}")
  private String uri;

  @Bean
  public Queue queue() {
    return new Queue(queueName);
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory cf = new CachingConnectionFactory();
    cf.setUsername(userName);
    cf.setPassword(password);
    cf.setUri(uri);
    return cf;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
    containerFactory.setConnectionFactory(connectionFactory);
    return containerFactory;
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public Channel channel(ConnectionFactory cf) throws IOException, TimeoutException {
    CachingConnectionFactory cachingCF = (CachingConnectionFactory) cf;
    Connection conn = cachingCF.getRabbitConnectionFactory().newConnection();
    return conn.createChannel();
  }
}

