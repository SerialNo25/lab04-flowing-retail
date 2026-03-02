package io.flowing.retail.notification.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.flowing.retail.notification.writer.NotificationWriter;

@Service
public class EventNotificationService {

  @Autowired
  private NotificationWriter notificationWriter;

  public void notifyStatusUpdate(String eventType, JsonNode payload) {
    String orderId = payload.path("orderId").asText("unknown-order");
    String statusMessage = switch (eventType) {
      case "OrderPlacedEvent" -> "Order " + orderId + " was placed.";
      case "PaymentReceivedEvent" -> "Payment received for order " + orderId + ".";
      case "GoodsFetchedEvent" -> "Goods fetched for order " + orderId + ".";
      case "GoodsShippedEvent" -> "Goods shipped for order " + orderId + ".";
      case "OrderCompletedEvent" -> "Order " + orderId + " is completed.";
      case "OrderCancelledEvent" -> "Order " + orderId + " was cancelled.";
      default -> "Received unsupported event type: " + eventType;
    };
    notificationWriter.writeStatusUpdate(statusMessage);
  }
}
