package io.flowing.retail.inventory.application;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.flowing.retail.inventory.domain.Item;
import io.flowing.retail.inventory.domain.PickOrder;

@Component
public class InventoryService {

  private final Map<String, Integer> availableStock = new ConcurrentHashMap<>();
  private final Map<String, Integer> soldStock = new ConcurrentHashMap<>();

  public InventoryService() {
    availableStock.put("article1", 25);
    availableStock.put("article2", 40);
  }

  /**
   * reserve goods on stock for a defined period of time
   *
   * @param reason A reason why the goods are reserved (e.g. "customer order")
   * @param refId A reference id fitting to the reason of reservation (e.g. the order id), needed to find reservation again later
   * @param expirationDate Date until when the goods are reserved, afterwards the reservation is removed
   * @return if reservation could be done successfully
   */
  public boolean reserveGoods(List<Item> items, String reason, String refId, LocalDateTime expirationDate) {
    // TODO: Implement
    return true;
  }

  /**
   * Order to pick the given items in the warehouse. The inventory is decreased.
   * Reservation fitting the reason/refId might be used to fulfill the order.
   *
   * If no enough items are on stock - an exception is thrown.
   * Otherwise a unique pick id is returned, which can be used to
   * reference the bunch of goods in the shipping area.
   *
   * @param items to be picked
   * @param reason for which items are picked (e.g. "customer order")
   * @param refId Reference id fitting to the reason of the pick (e.g. "order id"). Used to determine which reservations can be used.
   * @return a unique pick ID
   */
  public synchronized String pickItems(List<Item> items, String reason, String refId) {
    for (Item item : items) {
      int available = availableStock.getOrDefault(item.getArticleId(), 0);
      if (available < item.getAmount()) {
        throw new IllegalStateException("Not enough stock for article " + item.getArticleId() + ". Requested " + item.getAmount() + " but only " + available + " available.");
      }
    }

    for (Item item : items) {
      String articleId = item.getArticleId();
      int amount = item.getAmount();
      availableStock.put(articleId, availableStock.getOrDefault(articleId, 0) - amount);
      soldStock.put(articleId, soldStock.getOrDefault(articleId, 0) + amount);
    }

    PickOrder pickOrder = new PickOrder().setItems(items);
    System.out.println("# Items picked: " + pickOrder);
    return pickOrder.getPickId();
  }

  /**
   * New goods are arrived and inventory is increased
   */
  public synchronized void topUpInventory(String articleId, int amount) {
    availableStock.put(articleId, availableStock.getOrDefault(articleId, 0) + amount);
  }

  public synchronized Map<String, Integer> getAvailableStock() {
    return new HashMap<>(availableStock);
  }

  public synchronized Map<String, Integer> getSoldStock() {
    return new HashMap<>(soldStock);
  }

}
