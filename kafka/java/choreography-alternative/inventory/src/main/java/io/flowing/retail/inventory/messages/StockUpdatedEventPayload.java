package io.flowing.retail.inventory.messages;

import java.util.HashMap;
import java.util.Map;

public class StockUpdatedEventPayload {

  private Map<String, Integer> available = new HashMap<>();
  private Map<String, Integer> sold = new HashMap<>();

  public Map<String, Integer> getAvailable() {
    return available;
  }

  public StockUpdatedEventPayload setAvailable(Map<String, Integer> available) {
    this.available = available;
    return this;
  }

  public Map<String, Integer> getSold() {
    return sold;
  }

  public StockUpdatedEventPayload setSold(Map<String, Integer> sold) {
    this.sold = sold;
    return this;
  }
}
