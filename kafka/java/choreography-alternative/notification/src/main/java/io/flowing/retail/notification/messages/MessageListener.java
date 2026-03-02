package io.flowing.retail.notification.messages;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.flowing.retail.notification.application.EventNotificationService;

@Component
public class MessageListener {

  private static final Set<String> HANDLED_EVENT_TYPES = Set.of(
      "OrderPlacedEvent",
      "PaymentReceivedEvent",
      "GoodsFetchedEvent",
      "GoodsShippedEvent",
      "OrderCompletedEvent",
      "OrderCancelledEvent");

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EventNotificationService eventNotificationService;

  @KafkaListener(id = "notification", topics = "flowing-retail")
  public void onMessage(String messageJson, @Header("type") String messageType) throws Exception {
    if (!HANDLED_EVENT_TYPES.contains(messageType)) {
      return;
    }

    JsonNode message = objectMapper.readTree(messageJson);
    JsonNode payload = message.path("data");
    eventNotificationService.notifyStatusUpdate(messageType, payload);
  }
}
