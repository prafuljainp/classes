import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    public void sendEmail(String subject, String body) throws MessagingException {
        System.out.println("📧 Preparing email...");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", ConfigLoader.get("mail.smtp.host"));
        props.put("mail.smtp.port", ConfigLoader.get("mail.smtp.port"));
        props.put("mail.debug", "true");  // Enable debug output to console

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    ConfigLoader.get("mail.smtp.username"),
                    ConfigLoader.get("mail.smtp.password")
                );
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ConfigLoader.get("mail.smtp.username")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ConfigLoader.get("mail.recipient")));
            message.setSubject(subject);
            message.setText(body);

            System.out.println("📨 Sending email to: " + ConfigLoader.get("mail.recipient"));
            Transport.send(message);
            System.out.println("Email sent successfully.");

        } catch (AuthenticationFailedException e) {
            System.err.println(" Authentication failed: Check your email/password (app password required for Gmail).");
            throw e;
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw e;
        }
    }
}
