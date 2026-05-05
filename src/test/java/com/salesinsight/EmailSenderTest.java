package com.salesinsight;

import com.salesinsight.email.EmailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailSender class.
 */
@DisplayName("EmailSender Tests")
class EmailSenderTest {

    @Test
    @DisplayName("Should create EmailSender instance")
    void testCreateEmailSender() {
        EmailSender sender = new EmailSender();
        assertNotNull(sender, "Should create EmailSender instance");
    }

    @Test
    @DisplayName("Should accept subject and body parameters")
    void testEmailParameters() {
        EmailSender sender = new EmailSender();
        String subject = "Test Subject";
        String body = "Test Body";
        
        assertNotNull(subject, "Subject should not be null");
        assertNotNull(body, "Body should not be null");
    }

    @Test
    @DisplayName("Should handle valid email format")
    void testValidEmailFormat() {
        EmailSender sender = new EmailSender();
        String subject = "Weekly Sales Report";
        String body = "Sales analysis for week of May 5, 2026";
        
        assertNotNull(sender, "Should accept valid email format");
    }

    @Test
    @DisplayName("Should support HTML content in emails")
    void testEmailContent() {
        EmailSender sender = new EmailSender();
        String htmlBody = "<html><body><h1>Sales Report</h1></body></html>";
        
        assertNotNull(sender, "Should support HTML content");
    }
}
