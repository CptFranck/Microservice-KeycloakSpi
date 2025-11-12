package com.CptFranck.KeycloakSpi.client;


import com.CptFranck.dto.KeycloakUserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.models.UserModel;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class CustomerServiceClient {

    private final String customerServiceUrl;
    private final HttpClient httpClient;

    public CustomerServiceClient(String url) {
        this.httpClient = HttpClient.newHttpClient();
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

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(keycloakUserDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't serialize keycloak user dto", e);
        }
    }

    private void sendAsyncRequest(String path, String method, String body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(customerServiceUrl + path))
                .header("Content-Type", "application/json");

            if (body != null)
                builder.method(method, HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            else
                builder.method(method, HttpRequest.BodyPublishers.noBody());

            httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
