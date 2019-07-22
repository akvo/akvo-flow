/*
 * Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo FLOW.
 *
 * Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 * the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 * either version 3 of the License or any later version.
 *
 * Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License included below for more details.
 *
 * The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.flow.rest.security.oidc;

import com.auth0.AuthenticationController;
import com.gallatinsystems.common.util.PropertyUtil;

public class AppConfig {
    private String domain;
    private String clientId;
    private String clientSecret;

    AuthenticationController authenticationController() {
        if (domain == null || clientId == null || clientSecret == null) {
            return null;
        }
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
