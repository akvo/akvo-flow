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
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
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

        Map paramMap = req.getParameterMap();
        String incomingHash = null;
        long incomingTimestamp = 0;
        List<String> names = new ArrayList<String>();
        if (paramMap != null) {
            names.addAll(paramMap.keySet());
            Collections.sort(names);
            StringBuilder builder = new StringBuilder();
            for (String name : names) {
                if (!RestRequest.HASH_PARAM.equals(name)) {
                    if (builder.length() > 0) {
                        builder.append("&");
                    }

                    if (RestRequest.TIMESTAMP_PARAM.equals(name)) {
                        String timestamp = ((String[]) paramMap.get(name))[0];
                        try {
                            DateFormat df = new SimpleDateFormat(
                                    "yyyy/MM/dd HH:mm:ss");
                            df.setTimeZone(TimeZone.getTimeZone("GMT"));
                            incomingTimestamp = df.parse(timestamp).getTime();
                        } catch (Exception e) {
                            log.warning("Recived rest api request with invalid timestamp");
                            return false;
                        }
                    }
                    String[] vals = ((String[]) paramMap.get(name));
                    int count = 0;
                    for (String v : vals) {
                        if (count > 0) {
                            builder.append("&");
                        }
                        builder.append(name).append("=").append(URLEncoder.encode(v, "UTF-8"));
                        count++;
                    }
                } else {
                    incomingHash = ((String[]) paramMap.get(name))[0];
                    incomingHash = incomingHash.replaceAll(" ", "+");
                }
            }

            if (incomingHash != null) {
                String ourHash = MD5Util.generateHMAC(builder.toString(),
                        privateKey);
                if (ourHash == null) {
                    // Do something but for now return false;
                    return false;
                }

                if (ourHash.equals(incomingHash)) {
                    return isTimestampValid(incomingTimestamp);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean isTimestampValid(long theirTime) {
        long time = System.currentTimeMillis();
        if (Math.abs(time - theirTime) > MAX_TIME) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void init(FilterConfig arg) throws ServletException {
        String enabledFlag = PropertyUtil.getProperty(ENABLED_PROP);
        if (enabledFlag != null) {
            try {
                isEnabled = Boolean.parseBoolean(enabledFlag.trim());
            } catch (Exception e) {
                log.severe("Could not parse " + ENABLED_PROP + " value of "
                        + enabledFlag);
                isEnabled = false;
            }
        }
        privateKey = PropertyUtil.getProperty(REST_PRIVATE_KEY_PROP);
    }

    @Override
    public void destroy() {
    }
}
