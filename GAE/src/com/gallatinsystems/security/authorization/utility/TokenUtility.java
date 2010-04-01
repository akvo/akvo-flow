package com.gallatinsystems.security.authorization.utility;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Properties;

import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;

public class TokenUtility {

	public String generateSessionTokenFromSingleUse(String singleUseToken) throws AuthenticationException, IOException, GeneralSecurityException {

		return AuthSubUtil.exchangeForSessionToken(
				singleUseToken, getPrivateKey());

	}

	public PrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
		Properties props = System.getProperties();
		String storepassword = props.getProperty("storepass");
		String alias = props.getProperty("alias");
		String keypassword = props.getProperty("keypass");
		
		return  AuthSubUtil
				.getPrivateKeyFromKeystore("WEB-INF/watermapmonitordev.jks",
						storepassword, alias,
						keypassword);
	}

}
