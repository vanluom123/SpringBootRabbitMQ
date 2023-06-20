package com.javainuse.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class RabbitMQConfig {

  private static final String QUEUE_NAME = "javainuse.queue";
  private static final String DEAD_LETTER_QUEUE_NAME = "deadLetterQueue";
  private static final String EXCHANGE_NAME = "javainuse-direct-exchange";
  private static final String DEAD_LETTER_EXCHANGE_NAME = "deadLetterExchange";
  private static final String DEAD_LETTER_ROUTING_KEY = "deadLetter";
  private static final String ROUTING_KEY = "javainuse";

  @Value("${javainuse.rabbitmq.username}")
  private String userName;

  @Value("${javainuse.rabbitmq.password}")
  private String password;

  @Value("${javainuse.rabbitmq.uri}")
  private String uri;

  @Bean
  DirectExchange deadLetterExchange() {
    return new DirectExchange(DEAD_LETTER_EXCHANGE_NAME);
  }

  @Bean
  Queue dlq() {
    return QueueBuilder.durable(DEAD_LETTER_QUEUE_NAME)
        .build();
  }

  @Bean
  Queue queue() {
    return QueueBuilder.durable(QUEUE_NAME)
        .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME)
        .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
        .build();
  }

  @Bean
  DirectExchange exchange() {
    return new DirectExchange(EXCHANGE_NAME);
  }

  @Bean
  Binding DLQbinding(Queue dlq, DirectExchange deadLetterExchange) {
    return BindingBuilder.bind(dlq).to(deadLetterExchange).with(DEAD_LETTER_ROUTING_KEY);
  }

  @Bean
  Binding binding(Queue queue, DirectExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory cf = new CachingConnectionFactory();
    cf.setUsername(userName);
    cf.setPassword(password);
    cf.setUri(uri);
    cf.setChannelCheckoutTimeout(5000);
    cf.setConnectionTimeout(10000);
    return cf;
  }

  @Primary
  @Bean("rabbitmqTemplate")
  public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RetryTemplate retryTemplate = new RetryTemplate();
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(3);
    retryTemplate.setRetryPolicy(retryPolicy);

    FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(5000);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    rabbitTemplate.setRetryTemplate(retryTemplate);
    return rabbitTemplate;
  }

  @Bean
  public Channel channel(ConnectionFactory cf) throws IOException, TimeoutException {
    CachingConnectionFactory cacheCF = (CachingConnectionFactory) cf;
    Connection conn = cacheCF.getRabbitConnectionFactory().newConnection();
    return conn.createChannel();
  }
}
