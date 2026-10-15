package facade;

import facade.subsystem.InventoryService;
import facade.subsystem.NotificationService;
import facade.subsystem.PaymentProcessor;
import facade.subsystem.ShippingService;

public final class ShopFactory {

    private ShopFactory() {
    }

    public static OrderFacade createOrderFacade() {
        InventoryService inventory = new InventoryService();
        inventory.addProduct("LAPTOP", 350_000, 2);
        inventory.addProduct("MOUSE", 8_000, 10);
        inventory.addProduct("KEYBOARD", 15_000, 5);

        PaymentProcessor payments = new PaymentProcessor();
        payments.registerCard("4400111122223333", 500_000);
        payments.registerCard("4400999988887777", 20_000);

        return new OrderFacade(inventory, payments, new ShippingService(), new NotificationService());
    }
}
