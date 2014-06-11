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

package com.gallatinsystems.common.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Simple utility that can handle making http requests using the java HttpURLConnection class.
 * 
 * @author Christopher Fagiani
 */
public class HttpUtil {
    private static final int BUF_SIZE = 1024;

    /**
     * executes a post to the targetURL. The urlParmeters will be sent as POST data rather than on
     * the query string. UrlParameters should omit the leading "?" but should include the &
     * separator character. It is assumed that the urlParameters have already been url encoded.
     * 
     * @param targetURL
     * @param urlParameters
     * @return
     */
    public static byte[] doPost(String targetURL, String urlParameters,
            String contentType) {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            InputStream is = connection.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[BUF_SIZE];
            while ((bytesRead = is.read(buffer)) > 0) {
                bos.write(buffer, 0, bytesRead);
            }
            is.close();
            return bos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static byte[] doPost(String targetURL, String urlParameters) {
        return doPost(targetURL, urlParameters,
                "application/x-www-form-urlencoded");
    }
}
