package facade.subsystem;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShippingService {

    private static final Map<String, Double> COST_BY_CITY = Map.of(
            "Astana", 1000.0,
            "Almaty", 1500.0,
            "Shymkent", 2000.0
    );

    private final Set<String> activeShipments = new HashSet<>();
    private int nextTrackingNumber = 1;

    public boolean canDeliverTo(String city) {
        return COST_BY_CITY.containsKey(city);
    }

    public double calculateCost(String city) throws OrderException {
        if (!canDeliverTo(city)) {
            throw new OrderException("No delivery to " + city);
        }
        return COST_BY_CITY.get(city);
    }

    public String createShipment(String orderId, String city) throws OrderException {
        calculateCost(city);
        String trackingNumber = "TRK-" + nextTrackingNumber++;
        activeShipments.add(trackingNumber);
        System.out.println("  [Shipping] Shipment " + trackingNumber + " created for " + orderId + " to " + city);
        return trackingNumber;
    }

    public void cancelShipment(String trackingNumber) {
        if (activeShipments.remove(trackingNumber)) {
            System.out.println("  [Shipping] Shipment " + trackingNumber + " cancelled");
        }
    }
}
