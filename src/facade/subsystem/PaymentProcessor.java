package facade.subsystem;

import java.util.HashMap;
import java.util.Map;

public class PaymentProcessor {

    private record Payment(String cardNumber, double amount) { }

    private final Map<String, Double> cardBalances = new HashMap<>();
    private final Map<String, Payment> payments = new HashMap<>();
    private int nextTransactionNumber = 1;

    public void registerCard(String cardNumber, double balance) {
        cardBalances.put(cardNumber, balance);
    }

    public String charge(String cardNumber, double amount) throws OrderException {
        Double balance = cardBalances.get(cardNumber);
        if (balance == null) {
            throw new OrderException("Card " + mask(cardNumber) + " is not recognised");
        }
        if (balance < amount) {
            throw new OrderException("Insufficient funds on card " + mask(cardNumber));
        }
        cardBalances.put(cardNumber, balance - amount);
        String transactionId = "TX-" + nextTransactionNumber++;
        payments.put(transactionId, new Payment(cardNumber, amount));
        System.out.printf("  [Payment] Charged %.2f KZT to card %s (%s)%n", amount, mask(cardNumber), transactionId);
        return transactionId;
    }

    public void refund(String transactionId) {
        Payment payment = payments.remove(transactionId);
        if (payment == null) {
            return;
        }
        cardBalances.merge(payment.cardNumber(), payment.amount(), Double::sum);
        System.out.printf("  [Payment] Refunded %.2f KZT (%s)%n", payment.amount(), transactionId);
    }

    private String mask(String cardNumber) {
        return "****" + cardNumber.substring(Math.max(0, cardNumber.length() - 4));
    }
}
