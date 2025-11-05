package com.CptFranck.KeycloakSpi.listener;

import com.CptFranck.KeycloakSpi.client.CustomerServiceClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@Slf4j
public class CustomerEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;

    private final CustomerServiceClient customerClient;

    public CustomerEventListenerProvider(KeycloakSession session, CustomerServiceClient customerClient) {
        this.session = session;
        this.customerClient = customerClient;
    }

    @Override
    public void onEvent(Event event) {

        UserModel user = getUserModelFromEvent(event);
        switch (event.getType()) {
            case REGISTER:
                logEventAsJson(event, event.getType().name());
                customerClient.createCustomerFromKeycloak(user.getId(), user.getUsername(), user.getEmail());
                break;
            case UPDATE_PROFILE:
                logEventAsJson(event, event.getType().name());
                customerClient.updateCustomer(user.getId(), user.getUsername(), user.getEmail());
                break;
            case DELETE_ACCOUNT:
                logEventAsJson(event, event.getType().name());
                customerClient.deleteCustomer(event.getUserId());
                break;
            default:
                break;
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

        if (!"USER".equals(adminEvent.getResourceType().name())) return;
        String userId = adminEvent.getResourcePath().replace("users/", "");

        switch (adminEvent.getOperationType()) {
            case UPDATE:
                logEventAsJson(adminEvent, adminEvent.getOperationType().name());
                UserModel user = session.users().getUserById(
                        session.realms().getRealm(adminEvent.getRealmId()), userId);
                if (user != null)
                    customerClient.updateCustomer(user.getId(), user.getUsername(), user.getEmail());
                break;
            case DELETE:
                logEventAsJson(adminEvent, adminEvent.getOperationType().toString());
                customerClient.deleteCustomer(userId);
                break;
            default:
                break;
        }
    }

    @Override
    public void close() {}

    private UserModel getUserModelFromEvent(Event event){
        RealmModel realm = session.realms().getRealm(event.getRealmId());
        return session.users().getUserById(realm, event.getUserId());
    }

    private void logEventAsJson(Object obj, String eventType) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String jsonEvent = mapper.writeValueAsString(obj);
            log.info("Event {} JSON: {}", eventType, jsonEvent);
        } catch (JsonProcessingException e) {
            log.error("Error serializing event to JSON", e);
        }
    }
}
