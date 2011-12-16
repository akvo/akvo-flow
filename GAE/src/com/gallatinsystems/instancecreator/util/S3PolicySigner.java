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
