import java.io.*;
import java.net.*;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiClient {
    private final String apiUrl;
    /**
     * Constructs a GeminiClient with the specified API URL and API key.
     *
     * @param apiUrl The base URL of the Gemini API.
     * @param apiKey The API key for authentication.
     */


    public GeminiClient(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl + "?key=" + apiKey;
    }
    /**
     * Sends a chat completion request to the Gemini API with the provided messages.
     *
     * @param messages A list of messages to send to the API.
     * @return The response text from the API.
     * @throws IOException If an error occurs during the API call.
     */

    public String getChatCompletion(List<String> messages) throws IOException {
        URL url = new URL(apiUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        // Set up the connection properties
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        // Build the JSON body using org.json   
        // Create the JSON structure for the request body
        JSONArray partsArray = new JSONArray();
        for (String msg : messages) {
            JSONObject part = new JSONObject();
            part.put("text", msg);
            partsArray.put(part);
        }
        // Create the content object
        JSONObject content = new JSONObject();
        content.put("parts", partsArray);

        JSONArray contentsArray = new JSONArray();
        contentsArray.put(content);
        // Create the request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("contents", contentsArray);

        // Send the request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        // Log the request body for debugging
        // Handle the response
        int responseCode = conn.getResponseCode();
        InputStream responseStream = responseCode == 200 ?
                conn.getInputStream() : conn.getErrorStream();
        // Log the response code and body for debugging
        BufferedReader in = new BufferedReader(new InputStreamReader(responseStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        // Log the response for debugging
        if (responseCode != 200) {
            return "API call failed with status " + responseCode + ":\n" + response;
        }
        System.out.println("API call successful. Response: " + response);
        // Parse the response
        JSONObject json = new JSONObject(response.toString());
        JSONArray candidates = json.getJSONArray("candidates");
        // Check if candidates are present in the response
        if (!candidates.isEmpty()) {
            JSONObject candidate = candidates.getJSONObject(0);
            JSONObject replyContent = candidate.getJSONObject("content");
            JSONArray replyParts = replyContent.getJSONArray("parts");
            // Check if parts are present in the reply
            if (!replyParts.isEmpty()) {
                return replyParts.getJSONObject(0).getString("text").trim();
            }
        }
        // If no valid response is found, return a default message
        return "No valid response from Gemini.";
    }
}
