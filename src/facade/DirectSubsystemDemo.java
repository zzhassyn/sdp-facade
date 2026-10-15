package facade;

import facade.subsystem.InventoryService;
import facade.subsystem.ShippingService;

public class DirectSubsystemDemo {

    public static void main(String[] args) {
        InventoryService inventory = new InventoryService();
        inventory.addProduct("MOUSE", 8_000, 10);
        inventory.addProduct("MOUSE", 8_000, 5);
        System.out.println("Mouse stock after restock: " + inventory.getStock("MOUSE"));

        ShippingService shipping = new ShippingService();
        System.out.println("Can deliver to Shymkent? " + shipping.canDeliverTo("Shymkent"));
        System.out.println("Can deliver to Paris?    " + shipping.canDeliverTo("Paris"));
    }
}
