package com.salesinsight;

import com.salesinsight.analysis.InsightAnalyzer;
import com.salesinsight.config.ConfigLoader;
import com.salesinsight.crm.CRMDataFetcher;
import com.salesinsight.email.EmailSender;
import com.salesinsight.exception.APIException;
import com.salesinsight.exception.ConfigurationException;
import com.salesinsight.exception.DatabaseException;
import com.salesinsight.exception.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Main entry point for the Sales Insight Agent application.
 * Orchestrates the workflow: fetch CRM data, analyze with AI, and send an email report.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("Starting Sales Insight Agent v1.0.0");
        logger.info("========================================");

        try {
            logger.info("Step 1: Loading configuration...");
            validateConfiguration();
            logger.info("Configuration validated successfully");

            logger.info("Step 2: Fetching CRM sales data...");
            CRMDataFetcher fetcher = new CRMDataFetcher();
            List<String> salesData = fetcher.fetchData();

            if (salesData.isEmpty()) {
                logger.warn("No sales data retrieved from database");
                System.out.println("No sales data found in database. Please check your database configuration.");
                return;
            }

            logger.info("Retrieved {} sales records", salesData.size());

            logger.info("Step 3: Analyzing sales data with AI...");
            InsightAnalyzer analyzer = new InsightAnalyzer();
            String analysis = analyzer.analyze(String.join("\n", salesData));

            logger.info("Analysis completed successfully");
            logger.debug("Generated analysis: {}", analysis);

            logger.info("Step 4: Sending email report...");
            EmailSender emailSender = new EmailSender();
            String subject = ConfigLoader.get("mail.subject", "Weekly Sales Insight Report");
            emailSender.sendEmail(subject, analysis);

            logger.info("========================================");
            logger.info("Sales report generated and emailed successfully!");
            logger.info("========================================");
            System.out.println("\nProcess completed successfully!\n");
            System.out.println("Generated Analysis:\n");
            System.out.println(analysis);

        } catch (ConfigurationException e) {
            logger.error("Configuration error - application cannot start", e);
            System.err.println("\nConfiguration Error:\n");
            System.err.println(e.getMessage());
            System.err.println("\nPlease check your configuration in:");
            System.err.println("  - Environment variables");
            System.err.println("  - .env file");
            System.err.println("  - config.properties");
            System.exit(1);

        } catch (DatabaseException e) {
            logger.error("Database error occurred", e);
            System.err.println("\nDatabase Error:\n");
            System.err.println(e.getMessage());
            System.err.println("\nPlease check:");
            System.err.println("  - MySQL server is running");
            System.err.println("  - Database credentials are correct");
            System.err.println("  - Database and tables exist");
            System.exit(1);

        } catch (APIException e) {
            logger.error("API error occurred", e);
            System.err.println("\nAPI Error:\n");
            System.err.println(e.getMessage());
            System.err.println("\nPlease check:");
            System.err.println("  - Gemini API key is valid");
            System.err.println("  - API endpoint is accessible");
            System.err.println("  - You have sufficient API quota");
            System.exit(1);

        } catch (EmailException e) {
            logger.error("Email error occurred", e);
            System.err.println("\nEmail Error:\n");
            System.err.println(e.getMessage());
            System.err.println("\nPlease check:");
            System.err.println("  - Gmail account and password are correct");
            System.err.println("  - You are using an App Password, not regular password");
            System.err.println("  - 2-Factor Authentication is enabled");
            System.exit(1);

        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            System.err.println("\nUnexpected Error:\n");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void validateConfiguration() throws ConfigurationException {
        logger.debug("Validating configuration...");

        try {
            String dbUrl = ConfigLoader.get("db.url");
            String apiUrl = ConfigLoader.get("gemini.api.url");
            String apiKey = ConfigLoader.get("gemini.api.key");
            String mailPassword = ConfigLoader.get("mail.smtp.password");

            if (isBlankOrPlaceholder(dbUrl)) {
                throw new ConfigurationException("Database URL is not configured");
            }

            if (isBlankOrPlaceholder(apiUrl)) {
                throw new ConfigurationException("Gemini API URL is not configured");
            }

            if (isBlankOrPlaceholder(apiKey)) {
                throw new ConfigurationException("Gemini API key is not configured");
            }

            if (isBlankOrPlaceholder(mailPassword)) {
                throw new ConfigurationException("Email password is not configured");
            }

            logger.debug("Configuration validation passed");

        } catch (Exception e) {
            if (e instanceof ConfigurationException) {
                throw e;
            }
            throw new ConfigurationException("Configuration validation failed: " + e.getMessage(), e);
        }
    }

    private static boolean isBlankOrPlaceholder(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        String normalized = value.trim().toLowerCase();
        return normalized.startsWith("your_")
                || normalized.startsWith("your-")
                || normalized.startsWith("recipient_")
                || normalized.contains("your_api_key");
    }
}
