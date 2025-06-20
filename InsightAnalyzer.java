import java.io.IOException;
import java.util.List;


public class InsightAnalyzer {
    /**
     * Analyzes CRM sales data using the Gemini API.
     * This method sends a prompt to the Gemini API and returns the analysis result.
     * @param prompt The prompt containing the CRM sales data to analyze.
     * @return The analysis result as a String.
     */
    public String analyze(String prompt) {
        try {
           GeminiClient client = new GeminiClient(
    ConfigLoader.get("gemini.api.url"),
    ConfigLoader.get("gemini.api.key")
);  
            List<String> messages = List.of(
                    "You are an assistant that analyzes CRM sales data.",
                    prompt
            );
            System.out.println(" Analyzing CRM sales data with Gemini API...");
            return client.getChatCompletion(messages);

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();  
        }   
    }
}
