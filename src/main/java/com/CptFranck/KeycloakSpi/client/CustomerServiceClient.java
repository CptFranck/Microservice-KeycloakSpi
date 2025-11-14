package com.CptFranck.KeycloakSpi.client;


import com.CptFranck.dto.KeycloakUserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class CustomerServiceClient {

    private final Logger log;
    private final ObjectMapper mapper;
    private final HttpClient httpClient;

    private final String jwtUri;
    private final String clientId;
    private final String clientSecret;
    private final String customerServiceUrl;

    public CustomerServiceClient(String jwtUri, String clientId, String clientSecret, String url) {
        this.log = LoggerFactory.getLogger(CustomerServiceClient.class);
        this.mapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();

        this.jwtUri = jwtUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.customerServiceUrl = url;
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
            log.error(e.getMessage());
        }
    }

    private String getAccessToken() {
        try {
            String requestBody = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s", clientId, clientSecret);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jwtUri))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new RuntimeException("Failed to get access token: " + response.body());

            JsonNode node = mapper.readTree(response.body());
            return node.get("access_token").asText();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "";
    }
}
