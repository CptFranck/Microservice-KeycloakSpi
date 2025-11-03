package com.CptFranck.KeycloakSpi.listener;

import com.CptFranck.KeycloakSpi.client.CustomerServiceClient;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomerEventListenerProviderFactory implements EventListenerProviderFactory {

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        CustomerServiceClient client = new CustomerServiceClient("http://customer-service:8080/api/customers");
        return new CustomerEventListenerProvider(keycloakSession, client);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "customer-event-listener";
    }
}
