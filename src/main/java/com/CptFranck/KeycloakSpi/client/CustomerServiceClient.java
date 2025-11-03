package com.CptFranck.KeycloakSpi.client;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CustomerServiceClient {

    private final HttpClient httpClient;
    private final String customerServiceUrl;

    public CustomerServiceClient(String url) {
        this.httpClient = HttpClient.newBuilder().build();
        this.customerServiceUrl = url;
    }

    public void createCustomerFromKeycloak(String id, String username, String email) {
        String json = customerToJson(id, username, email);
        sendAsyncRequest("/customers", "POST", json);
    }

    public void updateCustomer(String id, String username, String email) {
        String json = customerToJson(id, username, email);
        sendAsyncRequest("/customers/" + id, "PUT", json);    }

    public void deleteCustomer(String userId) {
        sendAsyncRequest("/customers/" + userId, "DELETE", null);
    }

    private String customerToJson(String id, String username, String email){
        return String.format(
                "{\"keycloakId\":\"%s\",\"username\":\"%s\",\"email\":\"%s\"}",
                id, username, email
        );
    }

    private void sendAsyncRequest(String path, String method, String body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(customerServiceUrl + path))
                    .header("Content-Type", "application/json");

            if (body != null)
                builder.method(method, HttpRequest.BodyPublishers.ofString(body));
            else
                builder.method(method, HttpRequest.BodyPublishers.noBody());

            httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response ->
                            System.out.println("CustomerService [" + method + " " + path + "] → " + response.statusCode()))
                    .exceptionally(ex -> {
                        System.err.println("Error calling customer service: " + ex.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("Error preparing request: " + e.getMessage());
        }
    }
}
