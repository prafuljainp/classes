import java.util.*;

public class Main {

    public static void main(String[] args) {
        // Ensure you have a config.properties file in the resources folder with the required properties
        try {
            CRMDataFetcher fetcher = new CRMDataFetcher();
            List<String> salesData = fetcher.fetchData();

            InsightAnalyzer analyzer = new InsightAnalyzer();
            String analysis = analyzer.analyze(String.join("\n", salesData));

            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail(ConfigLoader.get("mail.subject"), analysis);
            // Log the analysis result
            System.out.println(" Sales report generated and emailed successfully.");
            
        } catch (Exception e) {
            System.err.println(" Error: " + e.getMessage());    
            e.printStackTrace();
        }
    }
}
