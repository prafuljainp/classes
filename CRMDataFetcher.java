import java.sql.*;
import java.util.*;

public class CRMDataFetcher {

    public List<String> fetchData() throws SQLException {
        List<String> records = new ArrayList<>();

        String query = "SELECT customer_name, amount, date FROM sales_data";

        try (Connection conn = DriverManager.getConnection(
                     ConfigLoader.get("db.url"),
                     ConfigLoader.get("db.username"),
                     ConfigLoader.get("db.password"));
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String record = String.format("Customer: %s | Amount: ₹%.2f | Date: %s",
                        rs.getString("customer_name"),
                        rs.getDouble("amount"),
                        rs.getDate("date"));
                records.add(record);
            }

        } catch (SQLException e) {
            System.err.println(" Database error: " + e.getMessage());
            throw e;
        }

        return records;
    }
}
