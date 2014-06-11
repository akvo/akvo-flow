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

package org.waterforpeople.mapping.dataexport.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.gallatinsystems.common.util.MD5Util;
import com.gallatinsystems.framework.rest.RestRequest;

public class FlowRestClientHelper {

    private Map<String, String> paramMap;
    private String baseUrl;
    private String privateKey;

    public FlowRestClientHelper(String url, String key) {
        baseUrl = url;
        privateKey = key;
        paramMap = new HashMap<String, String>();
    }

    /**
     * invokes a remote REST api. If the url is longer than 2048 characters, this method will use
     * POST since that is too long for a GET
     * 
     * @param fullUrl
     * @return
     * @throws Exception
     */
    public String invoke() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        paramMap.put(RestRequest.TIMESTAMP_PARAM, df.format(new Date()));
        String fullUrl = baseUrl + "?" + formQueryString();
        if (fullUrl.length() > 2048) {
            return fetchDataFromServerPOST(fullUrl);
        } else {
            return fetchDataFromServerGET(fullUrl);
        }

    }

    private String formQueryString() throws Exception {
        List<String> names = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        if (paramMap != null) {
            names.addAll(paramMap.keySet());
            Collections.sort(names);
            for (String name : names) {
                if (!RestRequest.HASH_PARAM.equals(name)) {
                    if (builder.length() > 0) {
                        builder.append("&");
                    }
                    builder.append(name)
                            .append("=")
                            .append(URLEncoder.encode(paramMap.get(name),
                                    "UTF-8"));
                }
            }
        }
        builder.append(RestRequest.HASH_PARAM).append("=")
                .append(MD5Util.generateHMAC(builder.toString(), privateKey));
        return builder.toString();
    }

    /**
     * executes a post to invoke a rest api
     */
    private String fetchDataFromServerPOST(String fullUrl) throws Exception {
        BufferedReader reader = null;
        String result = null;
        try {
            String baseUrl = fullUrl;
            String queryString = null;
            if (fullUrl.contains("?")) {
                baseUrl = fullUrl.substring(0, fullUrl.indexOf("?"));
                queryString = fullUrl.substring(fullUrl.indexOf("?") + 1);
            }
            URL url = new URL(baseUrl);
            System.out.println("Calling: " + baseUrl + " with params: "
                    + queryString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Length",
                    "" + Integer.toString(queryString.getBytes().length));
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.addRequestProperty("Accept-Encoding", "gzip");
            conn.addRequestProperty("User-Agent", "gzip");

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(queryString);
            wr.flush();
            wr.close();
            InputStream instream = conn.getInputStream();
            String contentEncoding = conn.getHeaderField("Content-Encoding");

            if (contentEncoding != null
                    && contentEncoding.equalsIgnoreCase("gzip")) {
                reader = new BufferedReader(new InputStreamReader(
                        new GZIPInputStream(instream), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(instream,
                        "UTF-8"));
            }

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    /**
     * executes a GET to invoke a rest api
     */
    private String fetchDataFromServerGET(String fullUrl) throws Exception {
        BufferedReader reader = null;
        String result = null;
        try {
            URL url = new URL(fullUrl);
            System.out.println("Calling: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.addRequestProperty("Accept-Encoding", "gzip");
            conn.addRequestProperty("User-Agent", "gzip");
            InputStream instream = conn.getInputStream();
            String contentEncoding = conn.getHeaderField("Content-Encoding");

            if (contentEncoding != null
                    && contentEncoding.equalsIgnoreCase("gzip")) {
                reader = new BufferedReader(new InputStreamReader(
                        new GZIPInputStream(instream), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(instream,
                        "UTF-8"));
            }

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

}
