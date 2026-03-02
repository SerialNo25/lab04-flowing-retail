package io.flowing.retail.notification.writer;

import org.springframework.stereotype.Component;

@Component
public class ConsoleNotificationWriter implements NotificationWriter {

  @Override
  public void writeStatusUpdate(String message) {
    System.out.println(message);
  }
}
