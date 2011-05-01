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

public class TokenUtility {
	private static final Logger log = Logger.getLogger(TokenUtility.class
			.getName());
	public String generateSessionTokenFromSingleUse(String singleUseToken) throws AuthenticationException, IOException, GeneralSecurityException {
		PrivateKey pk = getPrivateKey();
		if(pk!=null){
			log.log(Level.INFO, "Got PK");
		}
		return AuthSubUtil.exchangeForSessionToken(
				singleUseToken, pk);

	}

	public PrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
		Properties props = System.getProperties();
		String storepassword = props.getProperty("storepass");
		String alias = props.getProperty("alias");
		String keypassword = props.getProperty("keypass");
		
		return  AuthSubUtil
				.getPrivateKeyFromKeystore(PropertyUtil.getProperty("keystore"),
						storepassword, alias,
						keypassword);
	}

}
