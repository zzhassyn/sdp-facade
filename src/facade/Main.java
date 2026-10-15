package facade;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        OrderFacade shop = ShopFactory.createOrderFacade();

        Customer aliya = new Customer("Aliya", "aliya@mail.kz", "Astana", "4400111122223333");
        Customer bekzat = new Customer("Bekzat", "bekzat@mail.kz", "Almaty", "4400999988887777");
        Customer dana = new Customer("Dana", "dana@mail.kz", "Paris", "4400111122223333");

        System.out.println("=== 1. Successful order ===");
        OrderResult first = shop.placeOrder(Map.of("LAPTOP", 1, "MOUSE", 2), aliya);
        System.out.println(first);

        System.out.println("\n=== 2. Payment declined -> stock is released (rollback) ===");
        System.out.println(shop.placeOrder(Map.of("KEYBOARD", 3), bekzat));

        System.out.println("\n=== 3. Out of stock ===");
        System.out.println(shop.placeOrder(Map.of("LAPTOP", 5), aliya));

        System.out.println("\n=== 4. No delivery to this city -> stock is released (rollback) ===");
        System.out.println(shop.placeOrder(Map.of("MOUSE", 1), dana));

        System.out.println("\n=== 5. Cancel the first order ===");
        System.out.println(shop.cancelOrder(first.orderId()));

        System.out.println("\n=== 6. Cancel it again ===");
        System.out.println(shop.cancelOrder(first.orderId()));

        System.out.println("\n=== 7. Invalid quantity is rejected ===");
        System.out.println(shop.placeOrder(Map.of("MOUSE", -3), aliya));
    }
}
