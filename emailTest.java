import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;


public class emailTest {
    public static void main(String[] args) {
        // Replace with your Gmail credentials and recipient's email
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "Your_gmail"; // Your Gmail
        String password = "Your_password"; // App Password
        String toEmail = "Recipients_email"; // Recipient's email address
        
        // Ensure you replace the above placeholders with actual values
        String subject = "Test Email";
        String body = "This is a test email sent from Java.";
        System.out.println("Preparing to send email...");
        // Set up the mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");    
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);  
            }
        });
        // Enable debug output to console
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);        
            System.out.println("✅ Email sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
