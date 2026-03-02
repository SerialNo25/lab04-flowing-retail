package io.flowing.retail.checkout.application;

import io.flowing.retail.checkout.domain.Item;
import io.flowing.retail.checkout.messages.StockUpdatedEventPayload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StockViewService {

  private Map<String, Integer> available = new HashMap<>();
  private Map<String, Integer> sold = new HashMap<>();

  public synchronized void update(StockUpdatedEventPayload payload) {
    available = new HashMap<>(payload.getAvailable());
    sold = new HashMap<>(payload.getSold());
  }

  public synchronized boolean canFulfill(List<Item> items) {
    for (Item item : items) {
      if (available.getOrDefault(item.getArticleId(), 0) < item.getAmount()) {
        return false;
      }
    }
    return true;
  }

  public synchronized StockUpdatedEventPayload snapshot() {
    return new StockUpdatedEventPayload()
            .setAvailable(new HashMap<>(available))
            .setSold(new HashMap<>(sold));
  }
}
