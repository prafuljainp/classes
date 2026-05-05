package com.salesinsight.analysis;

import com.salesinsight.api.GeminiClient;
import com.salesinsight.config.ConfigLoader;
import com.salesinsight.exception.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * InsightAnalyzer uses AI to analyze CRM sales data.
 * Sends sales data to Gemini API and returns insights.
 */
public class InsightAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(InsightAnalyzer.class);

    /**
     * Analyzes CRM sales data using the Gemini API.
     *
     * @param prompt The prompt containing the CRM sales data to analyze
     * @return The analysis result as a String
     * @throws APIException If the API call fails
     */
    public String analyze(String prompt) throws APIException {
        logger.info("Starting analysis of sales data");
        
        try {
            GeminiClient client = new GeminiClient(
                    ConfigLoader.get("gemini.api.url"),
                    ConfigLoader.get("gemini.api.key")
            );
            
            List<String> messages = List.of(
                    "You are an expert business analyst specializing in CRM sales data analysis. " +
                    "Analyze the following sales data and provide actionable insights including trends, " +
                    "top performers, and recommendations.",
                    prompt
            );
            
            logger.debug("Sending sales data to Gemini API for analysis");
            String analysis = client.getChatCompletion(messages);
            
            logger.info("Analysis completed successfully");
            logger.debug("Analysis result: {}", analysis);
            
            return analysis;
            
        } catch (APIException e) {
            logger.error("API error during analysis", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during analysis", e);
            throw new APIException("Analysis failed: " + e.getMessage(), e);
        }
    }
}
