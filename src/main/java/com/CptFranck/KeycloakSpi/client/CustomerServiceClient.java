package com.CptFranck.KeycloakSpi.client;


import com.CptFranck.dto.KeycloakUserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class CustomerServiceClient {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceClient.class);
    private final String jwtUri;
    private final String clientId;
    private final String clientSecret;
    private final String customerServiceUrl;
    private final HttpClient httpClient;

    public CustomerServiceClient(String tokenUrl, String clientId, String clientSecret, String url) {
        this.jwtUri = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.customerServiceUrl = url;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void createCustomerFromKeycloak(UserModel user) {
        String json = JsonFromUserModel(user);
        sendAsyncRequest("/keycloak-event/new-customer", "POST", json);
    }

    public void updateCustomer(UserModel user) {
        String json = JsonFromUserModel(user);
        sendAsyncRequest("/keycloak-event/update-customer", "PUT", json);
    }

    public void deleteCustomer(String userId) {
        sendAsyncRequest("/keycloak-event/delete-customer/" + userId, "DELETE", null);
    }

    private String JsonFromUserModel(UserModel user){

        KeycloakUserDto keycloakUserDto = KeycloakUserDto.builder()
                .keycloakId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(keycloakUserDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't serialize keycloak user dto", e);
        }
    }

    private void sendAsyncRequest(String path, String method, String body) {
        try {
            log.info("Sending {} request to {} with {}", method, customerServiceUrl + path, body);

            String token = getAccessToken();
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(customerServiceUrl + path))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token);

            if (body != null)
                builder.method(method, HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            else
                builder.method(method, HttpRequest.BodyPublishers.noBody());

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
                log.error("Failed : HTTP error code : {}", response.statusCode());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private String getAccessToken() throws Exception {
        String requestBody = String.format(
                "grant_type=client_credentials&client_id=%s&client_secret=%s",
                clientId, clientSecret
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jwtUri))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new RuntimeException("Failed to get access token: " + response.body());

        JsonObject json = Json.createReader(new StringReader(response.body())).readObject();
        return json.getString("access_token");
    }
}
