package com.salesinsight.crm;

import com.salesinsight.config.ConfigLoader;
import com.salesinsight.exception.DatabaseException;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches CRM sales data from the database.
 */
public class CRMDataFetcher {
    private static final Logger logger = LoggerFactory.getLogger(CRMDataFetcher.class);
    private static final String QUERY = "SELECT customer_name, amount, date FROM sales_data ORDER BY date DESC";

    @Retry(name = "database", fallbackMethod = "fallback")
    public List<String> fetchData() {
        logger.info("Fetching CRM sales data from database...");
        List<String> records = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(
                ConfigLoader.get("db.url"),
                ConfigLoader.get("db.username"),
                ConfigLoader.get("db.password"));
             PreparedStatement stmt = conn.prepareStatement(QUERY);
             ResultSet rs = stmt.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                String record = String.format("Customer: %s | Amount: %.2f | Date: %s",
                        rs.getString("customer_name"),
                        rs.getDouble("amount"),
                        rs.getDate("date"));
                records.add(record);
                count++;
            }

            logger.info("Successfully fetched {} records from database", count);
            return records;

        } catch (SQLException e) {
            logger.error("Database error while fetching sales data", e);
            throw new DatabaseException("Failed to fetch data from database: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching CRM data", e);
            throw new DatabaseException("Unexpected error: " + e.getMessage(), e);
        }
    }

    public List<String> fallback(Exception exception) {
        logger.warn("Fallback triggered for database fetch after retries exhausted", exception);
        return new ArrayList<>();
    }
}
