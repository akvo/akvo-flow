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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class S3Util {

    private static Logger log = Logger.getLogger(S3Util.class.getName());

    private static final long EXPIRE_DATE = 2145916800; // 2038-01-01 00:00:00 +000
    private static final String S3_URL = "https://%s.s3.amazonaws.com/%s";
    private static final String GET_PAYLOAD = "GET\n\n\n%s\n/%s/%s";
    private static final String BROWSER_GET_PAYLOAD = "GET\n\n\n" + EXPIRE_DATE + "\n/%s/%s";
    private static final String PUT_PAYLOAD_PUBLIC = "PUT\n%s\n%s\n%s\nx-amz-acl:public-read\n/%s/%s";
    private static final String PUT_PAYLOAD_PRIVATE = "PUT\n%s\n%s\n%s\n/%s/%s";
    private static final String PUT_PAYLOAD_ACL = "PUT\n\n\n%s\nx-amz-acl:%s\n/%s/%s?acl";
    private static final String GET_PAYLOAD_ACL = "GET\n\n\n%s\n/%s/%s?acl";

    public enum ACL {
        PRIVATE("private"), PUBLIC_READ("public-read");
        private String val;

        ACL(String v) {
            this.val = v;
        }

        @Override
        public String toString() {
            return val;
        }
    }

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
        conn.addRequestProperty("Cache-Control", "no-cache,max-age=0");
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

        final byte[] md5Raw = MD5Util.md5(data);
        final String md5Base64 = Base64.encodeBase64String(md5Raw).trim();
        final String md5Hex = MD5Util.toHex(md5Raw);
        final String date = getDate();
        final String payloadStr = isPublic ? PUT_PAYLOAD_PUBLIC : PUT_PAYLOAD_PRIVATE;
        final String payload = String.format(payloadStr, md5Base64, contentType, date, bucketName,
                objectKey);
        final String signature = MD5Util.generateHMAC(payload, awsSecretKey);
        final URL url = new URL(String.format(S3_URL, bucketName, objectKey));

        OutputStream out = null;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-MD5", md5Base64);
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
            if (status != 200 && status != 201) {
                log.severe("Error uploading file: " + url.toString());
                log.severe(IOUtils.toString(conn.getInputStream()));
                return false;
            }
            String etag = conn.getHeaderField("ETag");
            etag = etag != null ? etag.replaceAll("\"", "") : null;// Remove quotes
            if (!md5Hex.equals(etag)) {
                log.severe("ETag comparison failed. Response ETag: " + etag +
                        "Locally computed MD5: " + md5Hex);
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

    public static String getBrowserLink(String bucketName, String objectKey) {

        final String awsAccessId = PropertyUtil.getProperty(AWS_ACCESS_ID);
        final String awsSecretKey = PropertyUtil.getProperty(AWS_SECRET_KEY);

        return getBrowserLink(bucketName, objectKey, awsAccessId, awsSecretKey);
    }

    public static String getBrowserLink(String bucketName, String objectKey, String awsAccessId,
            String awsSecretKey) {

        final String payload = String.format(BROWSER_GET_PAYLOAD, bucketName, objectKey);
        final String signature = MD5Util.generateHMAC(payload, awsSecretKey);

        final StringBuffer sb = new StringBuffer(String.format(S3_URL, bucketName, objectKey));
        sb.append("?AWSAccessKeyId=").append(awsAccessId);
        sb.append("&Expires=").append(EXPIRE_DATE);

        try {
            sb.append("&Signature=").append(URLEncoder.encode(signature, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.log(Level.SEVERE, "Error generating signature for browser URL: " + e.getMessage());
        }

        return sb.toString();
    }

    public static boolean putObjectAcl(String bucketName, String objectKey, ACL acl)
            throws IOException {

        final String awsAccessId = PropertyUtil.getProperty(AWS_ACCESS_ID);
        final String awsSecretKey = PropertyUtil.getProperty(AWS_SECRET_KEY);

        return putObjectAcl(bucketName, objectKey, acl, awsAccessId, awsSecretKey);
    }

    public static boolean putObjectAcl(String bucketName, String objectKey, ACL acl,
            String awsAccessId, String awsSecretKey) throws IOException {

        final String date = getDate();
        final URL url = new URL(String.format(S3_URL, bucketName, objectKey) + "?acl");
        final String payload = String.format(PUT_PAYLOAD_ACL, date, acl.toString(), bucketName,
                objectKey);
        final String signature = MD5Util.generateHMAC(payload, awsSecretKey);
        HttpURLConnection conn = null;
        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Date", date);
            conn.setRequestProperty("x-amz-acl", acl.toString());
            conn.setRequestProperty("Authorization", "AWS " + awsAccessId + ":" + signature);

            int status = conn.getResponseCode();

            if (status != 200 && status != 201) {
                log.severe("Error setting ACL for: " + url.toString());
                log.severe(IOUtils.toString(conn.getInputStream()));
                return false;
            }
            return true;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String getObjectAcl(String bucketName, String objectKey) throws IOException {

        final String awsAccessId = PropertyUtil.getProperty(AWS_ACCESS_ID);
        final String awsSecretKey = PropertyUtil.getProperty(AWS_SECRET_KEY);

        return getObjectAcl(bucketName, objectKey, awsAccessId, awsSecretKey);
    }

    public static String getObjectAcl(String bucketName, String objectKey, String awsAccessId,
            String awsSecretKey) throws IOException {

        final String date = getDate();
        final URL url = new URL(String.format(S3_URL, bucketName, objectKey) + "?acl");
        final String payload = String.format(GET_PAYLOAD_ACL, date, bucketName, objectKey);
        final String signature = MD5Util.generateHMAC(payload, awsSecretKey);

        InputStream in = null;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Date", date);
            conn.setRequestProperty("Authorization", "AWS " + awsAccessId + ":" + signature);

            in = new BufferedInputStream(conn.getInputStream());

            return IOUtils.toString(in);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error getting ACL for : " + url.toString(), e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            IOUtils.closeQuietly(in);
        }
    }

    private static String getDate() {
        final DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date()) + "GMT";
    }

}
