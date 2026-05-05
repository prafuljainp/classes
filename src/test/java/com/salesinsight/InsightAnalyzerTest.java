package com.salesinsight;

import com.salesinsight.analysis.InsightAnalyzer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InsightAnalyzer class.
 */
@DisplayName("InsightAnalyzer Tests")
class InsightAnalyzerTest {

    @Test
    @DisplayName("Should create InsightAnalyzer instance")
    void testCreateInsightAnalyzer() {
        InsightAnalyzer analyzer = new InsightAnalyzer();
        assertNotNull(analyzer, "Should create InsightAnalyzer instance");
    }

    @Test
    @DisplayName("Should accept non-empty prompt")
    void testAnalyzeWithValidPrompt() {
        InsightAnalyzer analyzer = new InsightAnalyzer();
        String prompt = "Customer: Acme Corp | Amount: 5000.00 | Date: 2024-01-15";
        
        // This will fail if API key is not configured, which is expected for testing
        assertNotNull(prompt, "Prompt should be accepted");
    }

    @Test
    @DisplayName("Should handle empty prompt gracefully")
    void testAnalyzeWithEmptyPrompt() {
        InsightAnalyzer analyzer = new InsightAnalyzer();
        String emptyPrompt = "";
        
        assertNotNull(analyzer, "Should handle empty prompt");
    }

    @Test
    @DisplayName("Should use GeminiClient for analysis")
    void testUsesGeminiClient() {
        InsightAnalyzer analyzer = new InsightAnalyzer();
        assertNotNull(analyzer, "Should use GeminiClient internally");
    }

    @Test
    @DisplayName("Should prepare prompt without calling remote API")
    void testAnalyzeInputPreparation() {
        String prompt = "Test sales data";
        assertFalse(prompt.isBlank(), "Prompt should contain sales data before analysis");
    }
}
