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

package com.gallatinsystems.framework.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.common.util.MD5Util;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * Handles verifying that the incoming request is authorized by checking the hash.
 * 
 * @author Christopher Fagiani
 */
public class RestAuthFilter implements Filter {

    private static final long MAX_TIME = 60 * 10 * 1000; // 10 minutes

    private static final Logger log = Logger.getLogger(RestAuthFilter.class
            .getName());
    private static final String ENABLED_PROP = "enableRestSecurity";
    public static final String REST_PRIVATE_KEY_PROP = "restPrivateKey";
    private String privateKey;
    private boolean isEnabled = false;

    /**
     * checks to see if auth is
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        if (isEnabled) {
            try {
                if (isAuthorized(req)) {
                    chain.doFilter(req, res);
                } else {
                    HttpServletResponse response = (HttpServletResponse) res;
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "Authorization failed");
                }
            } catch (Exception e) {
                log.severe("Auth failure " + e.getMessage());
                HttpServletResponse response = (HttpServletResponse) res;
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Authorization failed");
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private boolean isAuthorized(ServletRequest req) throws Exception {
        return validateHashParam(req) && validateTimeStamp(req);
    }

    public boolean validateHashParam(ServletRequest req) throws UnsupportedEncodingException {
        if (req.getParameterMap() == null || req.getParameter(RestRequest.HASH_PARAM) == null) {
            return false;
        }

        SortedMap<Object, String[]> sortedParamMap = new TreeMap<>();
        sortedParamMap.putAll(req.getParameterMap());

        StringBuilder builder = new StringBuilder();
        for (Object key : sortedParamMap.keySet()) {
            String paramKey = (String) key;
            if (RestRequest.HASH_PARAM.equals(paramKey)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append("&");
            }

            String[] vals = ((String[]) sortedParamMap.get(paramKey));
            int count = 0;
            for (String v : vals) {
                if (count > 0) {
                    builder.append("&");
                }
                builder.append(paramKey).append("=").append(URLEncoder.encode(v, "UTF-8"));
                count++;
            }
        }

        String incomingHash = ((String[]) sortedParamMap.get(RestRequest.HASH_PARAM))[0];
        incomingHash = incomingHash.replaceAll(" ", "+");

        String ourHash = MD5Util.generateHMAC(builder.toString(), privateKey);
        if (ourHash == null) {
            // Do something but for now return false;
            return false;
        }

        return ourHash.equals(incomingHash);
    }

    public boolean validateTimeStamp(ServletRequest req) {
        Map<Object, String[]> paramMap = req.getParameterMap();

        if(paramMap.isEmpty() || !paramMap.containsKey(RestRequest.TIMESTAMP_PARAM)) {
            return false;
        }

        String timestamp = ((String[]) paramMap.get(RestRequest.TIMESTAMP_PARAM))[0];
        try {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            long incomingTimeStamp = df.parse(timestamp).getTime();
            return (Math.abs(System.currentTimeMillis() - incomingTimeStamp) < MAX_TIME);
        } catch (ParseException e) {
            log.warning("Recived rest api request with invalid timestamp");
            return false;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String enabledFlag = null;
        if (filterConfig.getInitParameter(ENABLED_PROP) != null) {
            enabledFlag = filterConfig.getInitParameter(ENABLED_PROP);
        } else {
            enabledFlag = PropertyUtil.getProperty(ENABLED_PROP);
        }

        if (enabledFlag != null) {
            try {
                isEnabled = Boolean.parseBoolean(enabledFlag.trim());
            } catch (Exception e) {
                log.severe("Could not parse " + ENABLED_PROP + " value of "
                        + enabledFlag);
                isEnabled = false;
            }
        }

        if (filterConfig.getInitParameter(REST_PRIVATE_KEY_PROP) != null) {
            privateKey = filterConfig.getInitParameter(REST_PRIVATE_KEY_PROP);
        } else {
            privateKey = PropertyUtil.getProperty(REST_PRIVATE_KEY_PROP);
        }
    }

    @Override
    public void destroy() {
    }
}
