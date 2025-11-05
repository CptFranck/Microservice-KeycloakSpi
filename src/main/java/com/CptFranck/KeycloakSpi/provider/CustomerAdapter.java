//package com.CptFranck.CustomerService.provider;
//
//import com.CptFranck.CustomerService.entity.CustomerEntity;
//import org.keycloak.component.ComponentModel;
//import org.keycloak.models.KeycloakSession;
//import org.keycloak.models.RealmModel;
//import org.keycloak.storage.StorageId;
//import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
//
//public class CustomerAdapter extends AbstractUserAdapterFederatedStorage {
//
//    private final String id;
//    private final CustomerEntity customer;
//
//    public CustomerAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, CustomerEntity customer) {
//        super(session, realm, storageProviderModel);
//        this.customer = customer;
//        this.id = StorageId.keycloakId(storageProviderModel, String.valueOf(customer.getId()));
//    }
//
//    @Override
//    public String getId(){
//        return id;
//    }
//
//    @Override
//    public String getUsername() {
//        return customer.getUserName();
//    }
//
//    @Override
//    public void setUsername(String s) {
//        customer.setUserName(s);
//    }
//
//    @Override
//    public String getEmail(){
//        return customer.getEmail();
//    }
//
//    @Override
//    public void setEmail(String email){
//        customer.setEmail(email);
//    }
//}
