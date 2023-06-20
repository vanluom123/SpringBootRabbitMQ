package com.javainuse.util;

import org.springframework.context.ApplicationContext;

public class SpringUtils {
  private static ApplicationContext applicationContext;

  private SpringUtils() {
  }

  public static void setApplicationContext(ApplicationContext applicationContext) {
    SpringUtils.applicationContext = applicationContext;
  }

  public static Object getBean(String name) {
    return applicationContext.getBean(name);
  }

  public static <T> T getBean(Class<T> requiredType) {
    return applicationContext.getBean(requiredType);
  }
}
