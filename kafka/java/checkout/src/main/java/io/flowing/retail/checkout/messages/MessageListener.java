package io.flowing.retail.checkout.messages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.flowing.retail.checkout.application.StockViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

  @Autowired
  private StockViewService stockViewService;

  @Autowired
  private ObjectMapper objectMapper;

  @KafkaListener(id = "checkout", topics = "flowing-retail")
  public void messageReceived(String messagePayloadJson, @Header("type") String messageType) throws Exception {
    if (!"StockUpdatedEvent".equals(messageType)) {
      return;
    }

    Message<StockUpdatedEventPayload> message = objectMapper.readValue(messagePayloadJson, new TypeReference<Message<StockUpdatedEventPayload>>() {
    });
    stockViewService.update(message.getData());
  }
}
