package com.CptFranck.KeycloakSpi.listener;

import com.CptFranck.KeycloakSpi.client.CustomerServiceClient;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

public class CustomerEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;

    private final CustomerServiceClient customerClient;

    public CustomerEventListenerProvider(KeycloakSession session, CustomerServiceClient customerClient) {
        this.session = session;
        this.customerClient = customerClient;
    }

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {
            case REGISTER:
                customerClient.createCustomerFromKeycloak(event);
                break;
            case UPDATE_PROFILE:
                customerClient.updateCustomer(event);
                break;
            case DELETE_ACCOUNT:
                customerClient.deleteCustomer(event.getUserId());
                break;
            default:
                break;
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {}
}
