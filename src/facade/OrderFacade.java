package facade;

import facade.subsystem.InventoryService;
import facade.subsystem.NotificationService;
import facade.subsystem.OrderException;
import facade.subsystem.PaymentProcessor;
import facade.subsystem.ShippingService;

import java.util.HashMap;
import java.util.Map;

public class OrderFacade {

    private final InventoryService inventory;
    private final PaymentProcessor payments;
    private final ShippingService shipping;
    private final NotificationService notifications;

    private final Map<String, Order> orders = new HashMap<>();
    private int nextOrderNumber = 1;

    public OrderFacade(InventoryService inventory, PaymentProcessor payments,
                       ShippingService shipping, NotificationService notifications) {
        this.inventory = inventory;
        this.payments = payments;
        this.shipping = shipping;
        this.notifications = notifications;
    }

    public OrderResult placeOrder(Map<String, Integer> requestedCart, Customer customer) {
        if (requestedCart.isEmpty()) {
            return new OrderResult(false, null, "Cart is empty");
        }
        Map<String, Integer> cart = Map.copyOf(requestedCart);
        String orderId = "ORD-" + nextOrderNumber++;

        try {
            inventory.reserve(cart);
        } catch (OrderException e) {
            return fail(customer, orderId, e.getMessage());
        }

        double total;
        try {
            total = inventory.calculateSubtotal(cart) + shipping.calculateCost(customer.city());
        } catch (OrderException e) {
            inventory.release(cart);
            return fail(customer, orderId, e.getMessage());
        }

        String transactionId;
        try {
            transactionId = payments.charge(customer.cardNumber(), total);
        } catch (OrderException e) {
            inventory.release(cart);
            return fail(customer, orderId, e.getMessage());
        }

        String trackingNumber;
        try {
            trackingNumber = shipping.createShipment(orderId, customer.city());
        } catch (OrderException e) {
            payments.refund(transactionId);
            inventory.release(cart);
            return fail(customer, orderId, e.getMessage());
        }

        orders.put(orderId, new Order(orderId, customer, cart, total, transactionId, trackingNumber));
        sendEmailSafely(customer.email(), "Order " + orderId + " confirmed",
                String.format("Total: %.2f KZT, tracking: %s", total, trackingNumber));
        return new OrderResult(true, orderId, String.format("Order placed, total %.2f KZT", total));
    }

    public OrderResult cancelOrder(String orderId) {
        Order order = orders.remove(orderId);
        if (order == null) {
            return new OrderResult(false, orderId, "Order not found or already cancelled");
        }

        shipping.cancelShipment(order.trackingNumber());
        payments.refund(order.transactionId());
        inventory.release(order.cart());

        sendEmailSafely(order.customer().email(), "Order " + orderId + " cancelled",
                "Your money has been refunded");
        return new OrderResult(true, orderId, "Order cancelled and refunded");
    }

    private OrderResult fail(Customer customer, String orderId, String reason) {
        sendEmailSafely(customer.email(), "Order " + orderId + " failed", reason);
        return new OrderResult(false, null, reason);
    }

    private void sendEmailSafely(String email, String subject, String body) {
        try {
            notifications.sendEmail(email, subject, body);
        } catch (RuntimeException e) {
            System.out.println("  [Facade] Warning: e-mail to " + email + " was not sent: " + e.getMessage());
        }
    }
}
