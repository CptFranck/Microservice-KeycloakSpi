package com.CptFranck.KeycloakSpi.client;


import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CustomerServiceClient {

    private final String customerServiceUrl;
    private final HttpClient httpClient;

    public CustomerServiceClient(String url) {
        this.httpClient = HttpClient.newHttpClient();
        this.customerServiceUrl = url;
    }

    public void createCustomerFromKeycloak(String id, String username, String email) {
        log.info("Create customer from keycloak");
        String json = customerToJson(id, username, email);
        sendAsyncRequest("/customers", "POST", json);
    }

    public void updateCustomer(String id, String username, String email) {
        log.info("Update customer from keycloak");
        String json = customerToJson(id, username, email);
        sendAsyncRequest("/customers/" + id, "PUT", json);
    }

    public void deleteCustomer(String userId) {
        log.info("Delete customer from keycloak");
        sendAsyncRequest("/customers/" + userId, "DELETE", null);
    }

    private String customerToJson(String id, String username, String email){
        return String.format(
                "{\"keycloakId\":\"%s\",\"username\":\"%s\",\"email\":\"%s\"}",
                id, username, email
        );
    }

    private void sendAsyncRequest(String path, String method, String body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(customerServiceUrl + path))
                .header("Content-Type", "application/json");

        log.info("Sending request to {}{}", customerServiceUrl, path);

        if (body != null)
            builder.method(method, HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        else
            builder.method(method, HttpRequest.BodyPublishers.noBody());

        httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                .whenComplete((response, throwable) -> {
                    if (throwable != null)
                        log.error("Error calling customer service: {}", throwable.getMessage());
                    else
                        log.info("Successfully called customer service : {}", response.statusCode());
                });
    }
}
