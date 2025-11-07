package com.CptFranck.KeycloakSpi.listener;

import com.CptFranck.KeycloakSpi.client.CustomerServiceClient;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

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
                customerClient.createCustomerFromKeycloak(user);
                break;
            case UPDATE_PROFILE:
                customerClient.updateCustomer(user);
                break;
            case DELETE_ACCOUNT:
                customerClient.deleteCustomer(user.getId());
                break;
            default:
                break;
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

        if (!adminEvent.getResourceType().name().equals("USER")) return;

        String userId = adminEvent.getResourcePath().replace("users/", "");
        switch (adminEvent.getOperationType()) {
            case UPDATE:
                UserModel user = session.users().getUserById(session.realms().getRealm(adminEvent.getRealmId()),
                                                             userId);
                if (user != null)
                    customerClient.updateCustomer(user);
                else
                    System.out.println("user with id " + userId + " is null, cannot update customer");
                break;
            case DELETE:
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
}
