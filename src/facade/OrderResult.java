package facade;

public record OrderResult(boolean success, String orderId, String message) {

    @Override
    public String toString() {
        return (success ? "SUCCESS" : "FAILED") + (orderId != null ? " [" + orderId + "]" : "") + ": " + message;
    }
}
