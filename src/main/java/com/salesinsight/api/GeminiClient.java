package com.salesinsight.api;

import com.salesinsight.config.ConfigLoader;
import com.salesinsight.exception.APIException;
import io.github.resilience4j.retry.annotation.Retry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * GeminiClient handles communication with the Google Gemini API.
 * Sends prompts and receives AI-generated responses with retry logic.
 */
public class GeminiClient {
    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);
    private final String apiUrl;
    private final String apiKey;

    /**
     * Constructs a GeminiClient with the specified API URL and API key.
     *
     * @param apiUrl The base URL of the Gemini API
     * @param apiKey The API key for authentication
     */
    public GeminiClient(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        logger.debug("GeminiClient initialized with API URL: {}", apiUrl);
    }

    /**
     * Sends a chat completion request to the Gemini API with retry logic.
     *
     * @param messages A list of messages to send to the API
     * @return The response text from the API
     * @throws APIException If the API call fails
     */
    @Retry(name = "api")
    public String getChatCompletion(List<String> messages) throws APIException {
        logger.info("Sending {} messages to Gemini API", messages.size());
        
        try {
            URL url = new URL(apiUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            setupConnection(conn);
            
            JSONObject requestBody = buildRequestBody(messages);
            sendRequest(conn, requestBody);
            
            int responseCode = conn.getResponseCode();
            logger.debug("Gemini API response code: {}", responseCode);
            
            if (responseCode != 200) {
                String errorResponse = readErrorResponse(conn);
                logger.error("Gemini API error: {}", errorResponse);
                throw new APIException("API call failed with status " + responseCode + ": " + errorResponse, responseCode);
            }
            
            String response = readSuccessResponse(conn);
            logger.debug("Gemini API response received");
            
            String result = parseResponse(response);
            logger.info("Successfully received analysis from Gemini API");
            return result;
            
        } catch (APIException e) {
            throw e;
        } catch (IOException e) {
            logger.error("IO error during Gemini API call", e);
            throw new APIException("IO error during API call: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error during Gemini API call", e);
            throw new APIException("Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * Sets up the HTTPS connection properties.
     */
    private void setupConnection(HttpsURLConnection conn) throws IOException {
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("x-goog-api-key", apiKey);
        conn.setDoOutput(true);
        logger.debug("HTTPS connection configured");
    }

    /**
     * Builds the JSON request body for the API.
     */
    private JSONObject buildRequestBody(List<String> messages) {
        JSONArray partsArray = new JSONArray();
        for (String msg : messages) {
            JSONObject part = new JSONObject();
            part.put("text", msg);
            partsArray.put(part);
        }
        
        JSONObject content = new JSONObject();
        content.put("parts", partsArray);
        
        JSONArray contentsArray = new JSONArray();
        contentsArray.put(content);
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("contents", contentsArray);
        
        logger.debug("Request body built with {} parts", messages.size());
        return requestBody;
    }

    /**
     * Sends the request to the API.
     */
    private void sendRequest(HttpsURLConnection conn, JSONObject requestBody) throws IOException {
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        logger.debug("Request sent to Gemini API");
    }

    /**
     * Reads the error response from the API.
     */
    private String readErrorResponse(HttpsURLConnection conn) throws IOException {
        return readResponse(conn.getErrorStream());
    }

    /**
     * Reads the success response from the API.
     */
    private String readSuccessResponse(HttpsURLConnection conn) throws IOException {
        return readResponse(conn.getInputStream());
    }

    /**
     * Reads the response body from an input stream.
     */
    private String readResponse(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    /**
     * Parses the JSON response from the API.
     */
    private String parseResponse(String responseBody) throws APIException {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray candidates = json.getJSONArray("candidates");
            
            if (candidates.isEmpty()) {
                logger.warn("No candidates found in API response");
                return "No valid response from Gemini.";
            }
            
            JSONObject candidate = candidates.getJSONObject(0);
            JSONObject replyContent = candidate.getJSONObject("content");
            JSONArray replyParts = replyContent.getJSONArray("parts");
            
            if (!replyParts.isEmpty()) {
                String result = replyParts.getJSONObject(0).getString("text").trim();
                logger.debug("Parsed response successfully");
                return result;
            }
            
            logger.warn("No parts found in API response");
            return "No valid response from Gemini.";
            
        } catch (Exception e) {
            logger.error("Error parsing Gemini API response", e);
            throw new APIException("Error parsing API response: " + e.getMessage(), e);
        }
    }
}
