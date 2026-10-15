package facade.subsystem;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {

    private final Map<String, Double> prices = new HashMap<>();
    private final Map<String, Integer> stock = new HashMap<>();

    public void addProduct(String sku, double price, int quantity) {
        prices.put(sku, price);
        stock.merge(sku, quantity, Integer::sum);
    }

    public int getStock(String sku) {
        return stock.getOrDefault(sku, 0);
    }

    public void reserve(Map<String, Integer> cart) throws OrderException {
        for (Map.Entry<String, Integer> line : cart.entrySet()) {
            if (line.getValue() <= 0) {
                throw new OrderException("Quantity must be positive for " + line.getKey()
                        + " (got " + line.getValue() + ")");
            }
            if (!prices.containsKey(line.getKey())) {
                throw new OrderException("Unknown product: " + line.getKey());
            }
            if (getStock(line.getKey()) < line.getValue()) {
                throw new OrderException("Not enough stock for " + line.getKey()
                        + " (requested " + line.getValue() + ", available " + getStock(line.getKey()) + ")");
            }
        }
        cart.forEach((sku, quantity) -> stock.merge(sku, -quantity, Integer::sum));
        System.out.println("  [Inventory] Reserved " + cart.size() + " product line(s)");
    }

    public void release(Map<String, Integer> cart) {
        cart.forEach((sku, quantity) -> stock.merge(sku, quantity, Integer::sum));
        System.out.println("  [Inventory] Released " + cart.size() + " product line(s) back to stock");
    }

    public double calculateSubtotal(Map<String, Integer> cart) {
        double subtotal = 0;
        for (Map.Entry<String, Integer> line : cart.entrySet()) {
            subtotal += prices.get(line.getKey()) * line.getValue();
        }
        return subtotal;
    }
}
