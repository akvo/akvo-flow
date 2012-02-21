package com.gallatinsystems.framework.servlet;

import java.io.IOException;

import java.util.logging.Logger;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.common.util.PropertyUtil;
import com.google.gdata.util.common.util.Base64;

/**
 * Handles verifying that the incoming request is authorized by checking the
 * hash.
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class RestAuthFilter implements Filter {

	private static final String HASH_PARAM = "h";
	private static final String TIMESTAMP_PARAM ="ts";
	private static final Logger log = Logger.getLogger(RestAuthFilter.class.getName());
	private static final String ENABLED_PROP = "enableRestSecurity";
	private static final String REST_PRIVATE_KEY_PROP = "restPrivateKey";
	private String privateKey;
	private boolean isEnabled = false;

	/**
	 * checks to see if auth is 
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		if (isEnabled) {
			if(isAuthorized(req)){
				chain.doFilter(req, res);
			}else{			
				HttpServletResponse response = (HttpServletResponse) res;
		    	response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization failed");
			}
		} else {
			chain.doFilter(req, res);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean isAuthorized(ServletRequest req) {
		boolean authorized = false;
		Map paramMap = req.getParameterMap();
		String incomingHash = null;
		String incomingTimestamp = null;
		List<String> names = new ArrayList<String>();
		if(paramMap != null){
			names.addAll(paramMap.keySet());
			Collections.sort(names);
			StringBuilder builder = new StringBuilder();
			for(String name:names){		
				if(!HASH_PARAM.equals(name)){
					if(builder.length()>0){
						builder.append("&");
					}
					builder.append(name).append("=").append(paramMap.get(name));
					if(TIMESTAMP_PARAM.equals(name)){
						incomingTimestamp = (String)paramMap.get(name);
					}
				}else{
					incomingHash = (String)paramMap.get(name);
				}				
			}
			if(incomingHash != null){
				String ourHash =getHMAC(builder.toString());
				if(ourHash.equals(incomingHash)){
					return isTimestampValid(incomingTimestamp);
				}
			}
		}
		return authorized;
	}
	
	private boolean isTimestampValid(String val){
		return true;
	}

	private String getHMAC(String content) {
		Mac mac = Mac.getInstance("HmacSHA1");
		SecretKeySpec secret = new SecretKeySpec(privateKey.getBytes(),
				mac.getAlgorithm());
		mac.init(secret);
		byte[] digest = mac.doFinal(content.getBytes());
		return Base64.encode(digest);
	}

	@Override
	public void init(FilterConfig arg) throws ServletException {
		String enabledFlag =PropertyUtil.getProperty(ENABLED_PROP);
		if(enabledFlag != null){
			try{
				isEnabled =Boolean.parseBoolean(enabledFlag.trim());
			}catch(Exception e){
				log.severe("Could not parse "+ENABLED_PROP+" value of "+enabledFlag);
				isEnabled =false;
			}
		}
		privateKey = PropertyUtil.getProperty(REST_PRIVATE_KEY_PROP); 
	}

	@Override
	public void destroy() {
	}
}
