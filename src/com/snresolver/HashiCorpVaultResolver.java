package com.snresolver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HashiCorpVaultResolver {

    private final String agentAddress;

    public HashiCorpVaultResolver(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getPasswordById(String secretPath) throws Exception {
        String url = agentAddress + "/v1/secret/" + secretPath;
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("X-Vault-Request", "true"); // Optional, to mimic Vault Agent
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("GET");

        int status = conn.getResponseCode();
        if (status != 200) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                errorResponse.append(line);
            }
            reader.close();

            throw new RuntimeException("Vault error. HTTP status: " + status);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonObject data = json.getAsJsonObject("data").getAsJsonObject("data");
        //TODO Validate user and vault user are matching.
        return data.get("password").getAsString();
    }
}
