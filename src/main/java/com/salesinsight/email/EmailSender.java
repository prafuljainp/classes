package com.salesinsight.email;

import com.salesinsight.config.ConfigLoader;
import com.salesinsight.exception.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * EmailSender handles sending emails via Gmail SMTP.
 * Sends analysis results to configured recipients with error handling.
 */
public class EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    /**
     * Sends an email with the provided subject and body.
     *
     * @param subject The email subject
     * @param body    The email body content
     * @throws EmailException If the email fails to send
     */
    public void sendEmail(String subject, String body) throws EmailException {
        logger.info("Preparing email with subject: {}", subject);
        
        try {
            Session session = createEmailSession();
            Message message = createMessage(session, subject, body);
            
            logger.debug("Sending email to: {}", ConfigLoader.get("mail.recipient"));
            Transport.send(message);
            
            logger.info("Email sent successfully to {}", ConfigLoader.get("mail.recipient"));
            
        } catch (AuthenticationFailedException e) {
            logger.error("Gmail authentication failed - check credentials and app password", e);
            throw new EmailException("Authentication failed: Check your email credentials and app password. " +
                    "Gmail requires an App Password (not your regular password). " +
                    "Generate one at: https://myaccount.google.com/apppasswords", e);
        } catch (MessagingException e) {
            logger.error("Failed to send email", e);
            throw new EmailException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending email", e);
            throw new EmailException("Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * Creates and configures the email session.
     */
    private Session createEmailSession() {
        logger.debug("Creating email session with SMTP configuration");
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", ConfigLoader.get("mail.smtp.host"));
        props.put("mail.smtp.port", ConfigLoader.get("mail.smtp.port"));

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = ConfigLoader.get("mail.smtp.username");
                String password = ConfigLoader.get("mail.smtp.password");
                logger.debug("Authenticating with email: {}", username);
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /**
     * Creates the email message.
     */
    private Message createMessage(Session session, String subject, String body) throws MessagingException {
        Message message = new MimeMessage(session);
        
        message.setFrom(new InternetAddress(ConfigLoader.get("mail.smtp.username")));
        message.setRecipients(Message.RecipientType.TO, 
                InternetAddress.parse(ConfigLoader.get("mail.recipient")));
        message.setSubject(subject);
        
        // Send as plain text (can be modified to HTML if needed)
        message.setText(body);
        
        logger.debug("Email message created with subject: {}", subject);
        return message;
    }
}
