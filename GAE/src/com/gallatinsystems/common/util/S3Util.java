/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

import static com.gallatinsystems.common.Constants.AWS_ACCESS_ID;
import static com.gallatinsystems.common.Constants.AWS_SECRET_KEY;
import static com.gallatinsystems.common.Constants.CONNECTION_TIMEOUT;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public class S3Util {

    private static Logger log = Logger.getLogger(S3Util.class.getName());

    private static final String S3_URL = "https://%s.s3.amazonaws.com/%s";
    private static final String GET_PAYLOAD = "GET\n\n\n%s\n/%s/%s";
    private static final String PUT_PAYLOAD_PUBLIC = "PUT\n%s\n%s\n%s\nx-amz-acl:public-read\n/%s/%s";
    // FIXME: recover original payload "PUT\n%s\n%s\n%s\n/%s/%s";
    private static final String PUT_PAYLOAD_PRIVATE = "PUT\n\n%s\n%s\n/%s/%s";

    public static URLConnection getConnection(String bucketName, String objectKey)
            throws IOException {

        return getConnection(bucketName, objectKey, PropertyUtil.getProperty(AWS_ACCESS_ID),
                PropertyUtil.getProperty(AWS_SECRET_KEY));
    }

    public static URLConnection getConnection(String bucketName, String objectKey,
            String awsAccessKeyId,
            String awsAccessSecret) throws IOException {

        final String date = getDate();
        final String payload = String.format(GET_PAYLOAD, date, bucketName, objectKey);

        final String signature = MD5Util.generateHMAC(payload, awsAccessSecret);

        final URL url = new URL(String.format(S3_URL, bucketName, objectKey));
        final URLConnection conn = url.openConnection();

        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(CONNECTION_TIMEOUT);
        conn.setRequestProperty("Date", date);
        conn.setRequestProperty("Authorization", "AWS " + awsAccessKeyId + ":" + signature);

        return conn;
    }

    public static boolean put(String bucketName, String objectKey, byte[] data, String contentType,
            boolean isPublic) throws IOException {

        final String awsAccessId = PropertyUtil.getProperty(AWS_ACCESS_ID);
        final String awsSecretKey = PropertyUtil.getProperty(AWS_SECRET_KEY);

        return put(bucketName, objectKey, data, contentType, isPublic, awsAccessId, awsSecretKey);
    }

    public static boolean put(String bucketName, String objectKey, byte[] data, String contentType,
            boolean isPublic, String awsAccessId, String awsSecretKey) throws IOException {

        // FIXME: Include md5 hash
        // final String md5 = Base64.encodeBase64String(MD5Util.generateChecksum(data).getBytes());
        final String date = getDate();
        final String payloadStr = isPublic ? PUT_PAYLOAD_PUBLIC : PUT_PAYLOAD_PRIVATE;
        final String payload = String.format(payloadStr, contentType, date, bucketName, objectKey);
        final String signature = MD5Util.generateHMAC(payload, awsSecretKey);
        final URL url = new URL(String.format(S3_URL, bucketName, objectKey));

        OutputStream out = null;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            // FIXME: include checksum in request
            // conn.setRequestProperty("Content-MD5", md5);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Date", date);

            if (isPublic) {
                // If we don't send this header, the object will be private by default
                conn.setRequestProperty("x-amz-acl", "public-read");
            }

            conn.setRequestProperty("Authorization", "AWS " + awsAccessId + ":" + signature);

            out = new BufferedOutputStream(conn.getOutputStream());

            IOUtils.copy(new ByteArrayInputStream(data), out);
            out.flush();

            int status = conn.getResponseCode();
            if (status >= 400) {
                log.severe("Error uploading file: " + url.toString());
                log.severe(IOUtils.toString(conn.getInputStream()));
                return false;
            }
            return true;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            IOUtils.closeQuietly(out);
        }
    }

    private static String getDate() {
        final DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date()) + "GMT";
    }

}
