package com.CptFranck.KeycloakSpi.listener;

import com.CptFranck.KeycloakSpi.client.CustomerServiceClient;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomerEventListenerProviderFactory implements EventListenerProviderFactory {

    private String jwtUri;
    private String clientId;
    private String clientSecret;
    private String customerServiceUrl;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        CustomerServiceClient client = new CustomerServiceClient(jwtUri, clientId, clientSecret, customerServiceUrl);
        return new CustomerEventListenerProvider(keycloakSession, client);
    }

    @Override
    public void init(Config.Scope scope) {
        this.jwtUri = System.getenv().getOrDefault("JWT_URI", "http://localhost:8091/realms/ticketing-security-realm");
        this.clientId = System.getenv().getOrDefault("JWT_CLIENT_ID", "event-listener-client");
        this.clientSecret = System.getenv().getOrDefault("JWT_CLIENT_SECRET", "client-secret");
        this.customerServiceUrl = System.getenv().getOrDefault("CUSTOMER_SERVICE_API_URL", "http://localhost:8086/api/v1");
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
