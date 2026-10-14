package facade.subsystem;

public class NotificationService {

    public void sendEmail(String email, String subject, String body) {
        System.out.println("  [Email] To: " + email + " | " + subject + " | " + body);
    }
}
