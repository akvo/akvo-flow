/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.security.authorization.utility;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gallatinsystems.common.util.PropertyUtil;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;

/**
 * Utility class for generating a session token.
 */
public class TokenUtility {
    private static final Logger log = Logger.getLogger(TokenUtility.class
            .getName());

    public String generateSessionTokenFromSingleUse(String singleUseToken)
            throws AuthenticationException, IOException,
            GeneralSecurityException {
        PrivateKey pk = getPrivateKey();
        if (pk != null) {
            log.log(Level.INFO, "Got PK");
        }
        return AuthSubUtil.exchangeForSessionToken(singleUseToken, pk);

    }

    public PrivateKey getPrivateKey() throws IOException,
            GeneralSecurityException {
        Properties props = System.getProperties();
        String storepassword = props.getProperty("storepass");
        String alias = props.getProperty("alias");
        String keypassword = props.getProperty("keypass");

        return AuthSubUtil.getPrivateKeyFromKeystore(
                PropertyUtil.getProperty("keystore"), storepassword, alias,
                keypassword);
    }

}
