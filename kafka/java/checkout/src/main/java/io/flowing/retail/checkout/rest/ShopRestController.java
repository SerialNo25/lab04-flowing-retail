package io.flowing.retail.checkout.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.flowing.retail.checkout.application.StockViewService;
import io.flowing.retail.checkout.domain.Customer;
import io.flowing.retail.checkout.domain.Order;
import io.flowing.retail.checkout.messages.Message;
import io.flowing.retail.checkout.messages.MessageSender;
import io.flowing.retail.checkout.messages.StockUpdatedEventPayload;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RestController
public class ShopRestController {

  @Autowired
  private MessageSender messageSender;

  @Autowired
  private StockViewService stockViewService;

  @RequestMapping(path = "/api/cart/order", method = PUT)
  public String placeOrder(@RequestParam(value = "customerId") String customerId) {

    Order order = new Order();
    order.addItem("article1", 5);
    order.addItem("article2", 10);

    order.setCustomer(new Customer("Camunda", "Zossener Strasse 55\n10961 Berlin\nGermany"));

    StockUpdatedEventPayload stockSnapshot = stockViewService.snapshot();
    if (stockSnapshot.getAvailable().isEmpty()) {
      throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Stock is not synchronized yet. Please retry in a moment.");
    }
    if (!stockViewService.canFulfill(order.getItems())) {
      throw new ResponseStatusException(CONFLICT, "Order cannot be placed. Stock is exhausted.");
    }

    Message<Order> message = new Message<Order>("OrderPlacedEvent", order);
    messageSender.send(message);

    // note that we cannot easily return an order id here - as everything is asynchronous
    // and blocking the client is not what we want.
    // but we return an own correlationId which can be used in the UI to show status maybe later
    return "{\"traceId\": \"" + message.getTraceid() + "\"}";
  }

  @RequestMapping(path = "/api/stock", method = GET)
  public StockUpdatedEventPayload stock() {
    return stockViewService.snapshot();
  }

}
