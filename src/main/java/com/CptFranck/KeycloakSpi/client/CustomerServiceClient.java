package com.CptFranck.KeycloakSpi.client;

import org.keycloak.events.Event;

public class CustomerServiceClient {

    private final String customerServiceUrl;

    public CustomerServiceClient(String url) {
        this.customerServiceUrl = url;
    }

    public void createCustomerFromKeycloak(Event event) {
    }

    public void updateCustomer(Event event) {
    }

    public void deleteCustomer(String userId) {
    }
}
