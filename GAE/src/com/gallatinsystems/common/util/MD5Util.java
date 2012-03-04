package com.gallatinsystems.common.util;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.gdata.util.common.util.Base64;

/**
 * 
 * utility for generating checksums and digests
 * 
 * @author Christopher Fagiani
 * 
 */
public class MD5Util {
	private static final Logger log = Logger.getLogger(MD5Util.class.getName());

	/**
	 * generates a checksum based on an MD5 message digest
	 * 
	 * @param arr
	 * @return
	 */
	public static String generateChecksum(byte[] arr) {
		try {
			MessageDigest complete = MessageDigest.getInstance("MD5");
			complete.update(arr);
			byte[] digest = complete.digest();
			String result = "";
			for (int i = 0; i < digest.length; i++) {
				result += Integer.toString((digest[i] & 0xff) + 0x100, 16)
						.substring(1);
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.SEVERE, "Could not generate checksum", e);
		}
		return null;
	}
	
	public static String generateHMAC(String content, String privateKey){
		Mac mac;
		try {
			mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret = new SecretKeySpec(privateKey.getBytes(),
					mac.getAlgorithm());
			mac.init(secret);
			byte[] digest = mac.doFinal(content.getBytes());
			return Base64.encode(digest);
		} catch (NoSuchAlgorithmException e) {
			log.severe(e.getMessage() + e.getStackTrace());
		} catch (InvalidKeyException e) {
			log.severe(e.getMessage() + e.getStackTrace());
		}
		return null;
	}
}
