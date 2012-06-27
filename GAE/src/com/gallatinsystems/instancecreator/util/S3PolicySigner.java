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

package com.gallatinsystems.instancecreator.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

/**
 * 
 * Utility to generate S3 policy signatures for use in file upload.
 * 
 */
public class S3PolicySigner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	private String base64EncodedPolicyDocument = null;

	public String[] createPolicyString(String policy_document,
			String aws_secret_key) throws UnsupportedEncodingException,
			NoSuchAlgorithmException, InvalidKeyException {
		String policy = (new BASE64Encoder())
				.encode(policy_document.getBytes("UTF-8")).replaceAll("\n", "")
				.replaceAll("\r", "");
		base64EncodedPolicyDocument = policy;
		Mac hmac = Mac.getInstance("HmacSHA1");
		hmac.init(new SecretKeySpec(aws_secret_key.getBytes("UTF-8"),
				"HmacSHA1"));
		String signature = (new BASE64Encoder())
				.encode(hmac.doFinal(policy.getBytes("UTF-8")))
				.replaceAll("\n", "").replace("\r", "");
		String[] documents = new String[2];
		documents[0] = signature;
		documents[1] = base64EncodedPolicyDocument;
		return documents;
	}

}
