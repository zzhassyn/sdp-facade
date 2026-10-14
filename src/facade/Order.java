package facade;

import java.util.Map;

public record Order(String id, Customer customer, Map<String, Integer> cart,
                    double total, String transactionId, String trackingNumber) {
}
