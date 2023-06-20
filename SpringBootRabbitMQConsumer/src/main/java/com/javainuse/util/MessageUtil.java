package com.javainuse.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Component
public class MessageUtil {
  @Autowired
  private Channel channel;

  @Autowired
  private ObjectMapper mapper;

  public static <T> T convertMessageToObject(byte[] msg, Class<T> classType) throws IOException {
    String jsonStr = new String(msg, StandardCharsets.UTF_8);
    return new ObjectMapper().readValue(jsonStr, classType);
  }

  public <T> T consume(String queueName, Class<T> classType) throws IOException {
    AtomicReference<T> object = new AtomicReference<>();
    channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      object.set(mapper.readValue(message, classType));
      log.info("Received message: " + message);
    }, consumerTag -> {
    });
    return object.get();
  }

  public <T> CompletableFuture<T> consumeAsync(String queueName, Class<T> classType) {
    CompletableFuture<T> future = new CompletableFuture<>();
    try {
      channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        try {
          T object = mapper.readValue(message, classType);
          future.complete(object);
          log.info("Received message: " + message);
        } catch (Exception e) {
          future.completeExceptionally(e);
        }
      }, consumerTag -> {
      });
    } catch (IOException e) {
      future.completeExceptionally(e);
    }
    return future;
  }
}
