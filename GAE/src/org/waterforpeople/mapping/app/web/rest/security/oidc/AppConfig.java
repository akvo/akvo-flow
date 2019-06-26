package org.waterforpeople.mapping.app.web.rest.security.oidc;

import com.auth0.AuthenticationController;
import com.gallatinsystems.common.util.PropertyUtil;

public class AppConfig {
    private String domain;
    private String clientId;
    private String clientSecret;

    AuthenticationController authenticationController() {
        return AuthenticationController.newBuilder(domain, clientId, clientSecret)
                .build();
    }

    public AppConfig() {
        this.domain = PropertyUtil.getProperty("oidcDomain");
        this.clientId = PropertyUtil.getProperty("oidcClientId");
        this.clientSecret = PropertyUtil.getProperty("oidcClientSecret");
    }

    public String getDomain() {
        return domain;
    }

    public String getClientId() {
        return clientId;
    }

}
