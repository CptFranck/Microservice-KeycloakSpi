//package com.CptFranck.CustomerService.provider;
//
//import org.keycloak.component.ComponentModel;
//import org.keycloak.component.ComponentValidationException;
//import org.keycloak.models.KeycloakSession;
//import org.keycloak.models.RealmModel;
//import org.keycloak.models.utils.KeycloakModelUtils;
//import org.keycloak.provider.ProviderConfigProperty;
//import org.keycloak.provider.ProviderConfigurationBuilder;
//import org.keycloak.storage.UserStorageProvider;
//import org.keycloak.storage.UserStorageProviderFactory;
//import org.keycloak.storage.user.UserLookupProvider;
//import org.springframework.util.StringUtils;
//
//import java.util.List;
//
//public class CustomerStorageProviderFactory implements UserStorageProviderFactory<CustomerStorageProvider> {
//
//    public final static String PROVIDER_ID = "customer-storage-provider";
//
//    static final String USER_API_BASE_URL = "apiBaseUrl";
//    static final String CLIENT_ID = "clientId";
//    static final String EDIT_MODE = "editMode";
//    static final String USER_IMPORT = "importUsers";
//    static final String USER_CREATION_ENABLED = "userCreation";
//    static final String USE_PASSWORD_POLICY = "usePasswordPolicy";
//    static final String TRUST_EMAIL = "trustEmail";
//
//    @Override
//    public CustomerStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
//        return new CustomerStorageProvider();
//    }
//
//    @Override
//    public String getId() {
//        return PROVIDER_ID;
//    }
//
//    @Override
//    public String getHelpText() {
//        return "Customer Storage Provider";
//    }
//
//    @Override
//    public List<ProviderConfigProperty> getConfigProperties() {
//        return ProviderConfigurationBuilder.create()
//                .property(USER_API_BASE_URL, "apiBaseUrl", "api base url help", ProviderConfigProperty.STRING_TYPE, "http://localhost:8000", null)
//                .property(CLIENT_ID, "apiClientId", "api client id help", ProviderConfigProperty.CLIENT_LIST_TYPE, "", null)
//                .property(EDIT_MODE, "editMode", "edit mode help", ProviderConfigProperty.LIST_TYPE, UserStorageProvider.EditMode.READ_ONLY, List.of(UserStorageProvider.EditMode.READ_ONLY.name(), UserStorageProvider.EditMode.WRITABLE.name()))
//                .property(USER_IMPORT, "importUsers", "import users help", ProviderConfigProperty.BOOLEAN_TYPE, "false", null)
//                .property(USER_CREATION_ENABLED, "syncRegistrations", "sync registrations help", ProviderConfigProperty.BOOLEAN_TYPE, "false", null)
//                .property(USE_PASSWORD_POLICY, "validatePasswordPolicy", "validate password policy help", ProviderConfigProperty.BOOLEAN_TYPE, "false", null)
//                .property(TRUST_EMAIL, "trustEmail", "trust email help", ProviderConfigProperty.BOOLEAN_TYPE, "false", null)
//                .build();
//    }
//
//    @Override
//    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
//        if (config.getId() == null) {
//            config.setId(KeycloakModelUtils.generateShortId());
//        }
//
//        if (config.get(EDIT_MODE).equals(UserStorageProvider.EditMode.READ_ONLY.name()) && config.get(USER_CREATION_ENABLED, false)) {
//            throw new ComponentValidationException("Cannot set 'syncRegistrations' to true if 'editMode' is set to 'READ_ONLY'");
//        }
//
//        if (config.get(EDIT_MODE).equals(UserStorageProvider.EditMode.READ_ONLY.name()) && config.get(USE_PASSWORD_POLICY, false)) {
//            throw new ComponentValidationException("Cannot set 'validatePasswordPolicy' to true if 'editMode' is set to 'READ_ONLY'");
//        }
//    }
//
//    @Override
//    UserLookupProvider createUserLookupProvider(KeycloakSession keycloakSession) {
//
//    }
//}
