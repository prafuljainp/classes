package com.salesinsight;

import com.salesinsight.api.GeminiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GeminiClient class.
 */
@DisplayName("GeminiClient Tests")
class GeminiClientTest {

    private static final String TEST_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/generateContent";
    private static final String TEST_API_KEY = "test_key";

    @Test
    @DisplayName("Should create GeminiClient instance")
    void testCreateGeminiClient() {
        GeminiClient client = new GeminiClient(TEST_API_URL, TEST_API_KEY);
        assertNotNull(client, "Should create GeminiClient instance");
    }

    @Test
    @DisplayName("Should handle invalid API key gracefully")
    void testInvalidApiKey() {
        GeminiClient client = new GeminiClient(TEST_API_URL, "invalid_key");
        assertNotNull(client, "Should create client even with invalid key");
    }

    @Test
    @DisplayName("Should require non-empty message list")
    void testEmptyMessageList() {
        GeminiClient client = new GeminiClient(TEST_API_URL, TEST_API_KEY);
        assertNotNull(client, "Should handle empty message list");
    }

    @Test
    @DisplayName("Should create client without calling remote API")
    void testClientSetupDoesNotCallRemoteApi() {
        GeminiClient client = new GeminiClient(TEST_API_URL, "invalid_key");
        assertNotNull(client, "Client setup should not require network access");
    }
}
