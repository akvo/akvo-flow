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
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gallatinsystems.common.domain.UploadStatusContainer;

/**
 * Utility class to perform mime/multipart file uploads. This utility is STATEFUL in that upon
 * construction it binds a DataoutputStream to the output stream passed into the constructor. All
 * the "writeXXX" methods will write to this output stream.
 */
@Deprecated
public class UploadUtil {
    private static final String BOUNDRY = "***xxx";
    private static final int UPLOAD_TIMEOUT_MS = 120000;
    private static final String PREFIX = "--";
    private static final String ENDLINE = "\r\n";
    private static final int REDIRECT_CODE = 303;
    private static final int OK_CODE = 200;
    private static final String XML_MIME_TYPE = "text/xml";
    private static Logger logger = Logger.getLogger(UploadUtil.class.getName());

    private DataOutputStream out;

    private UploadUtil(OutputStream os) {
        out = new DataOutputStream(os);
    }

    /**
     * writes a form field name/value pair using MIME/MULTIPART encoding.
     */
    public void writeFormField(String name, String value) throws IOException {
        out.writeBytes(PREFIX);
        out.writeBytes(BOUNDRY);
        out.writeBytes(ENDLINE);

        out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
        out.writeBytes(ENDLINE);
        out.writeBytes(ENDLINE);

        out.writeBytes(value != null ? value : "");
        out.writeBytes(ENDLINE);
        out.flush();
    }

    /**
     * writes the contents of the fileContent string as an UTF-8 encoded file to the output stream.
     */
    public void writeFile(String key, String fileName, String fileContent,
            String mimeType) throws IOException {
        byte[] allBytes = fileContent.getBytes("UTF-8");
        writeFile(key, fileName, allBytes, mimeType);
    }

    /**
     * writes the contents of the byteArray to the output stream using MIME/MULTIPART encoding
     */
    public void writeFile(String key, String fileName, byte[] bytes,
            String mimeType) throws IOException {
        out.writeBytes(PREFIX);
        out.writeBytes(BOUNDRY);
        out.writeBytes(ENDLINE);
        String destName = fileName;
        if (destName.contains("/")) {
            destName = destName.substring(destName.lastIndexOf("/") + 1);
        } else if (destName.contains("\\")) {
            destName = destName.substring(destName.lastIndexOf("\\") + 1);
        }

        out.writeBytes("Content-Disposition: form-data; name=\"" + key
                + "\"; filename=\"" + destName + "\"");
        out.writeBytes(ENDLINE);
        if (mimeType != null) {
            out.writeBytes("Content-Type: " + mimeType);
            out.writeBytes(ENDLINE);
        }
        out.writeBytes(ENDLINE);

        out.write(bytes);

        out.writeBytes(ENDLINE);
        out.flush();
    }

    /**
     * writes the end of file markers to the output stream and then flushes and closes it.
     */
    public void close() throws IOException {
        out.writeBytes(PREFIX);
        out.writeBytes(BOUNDRY);
        out.writeBytes(PREFIX);
        out.writeBytes(ENDLINE);
        out.flush();
        out.close();
    }

    /**
     * creates an HttpUrlConnection to a remote url and sets it up to do a multipart upload.
     */
    public static HttpURLConnection createConnection(URL url)
            throws java.io.IOException {
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection httpConn = urlConn;
            httpConn.setRequestMethod("POST");
        }
        // connection level settings. This doesn't seem to have any effect with
        // https!
        urlConn.setConnectTimeout(UPLOAD_TIMEOUT_MS);
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setDefaultUseCaches(false);
        // set up request props
        urlConn.setRequestProperty("Accept", "*/*");
        urlConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + BOUNDRY);
        urlConn.setRequestProperty("Connection", "Keep-Alive");
        urlConn.setRequestProperty("Cache-Control", "no-cache");
        return urlConn;
    }

    /**
     * sends the zip file containing data/images to the server via an http upload
     * 
     * @param fileAbsolutePath
     */
    public static boolean sendStringAsFile(String fileName,
            String fileContents, String dir, String uploadUrl, String s3ID,
            String policy, String sig, String contentType) {

        try {
            HttpURLConnection conn = UploadUtil.createConnection(new URL(
                    uploadUrl));
            UploadUtil stream = new UploadUtil(conn.getOutputStream());
            stream.writeFormField("key", dir + "/${filename}");
            stream.writeFormField("AWSAccessKeyId", s3ID);
            stream.writeFormField("acl", "public-read");
            stream.writeFormField("success_action_redirect",
                    "http://www.gallatinsystems.com/SuccessUpload.html");
            stream.writeFormField("policy", policy);
            stream.writeFormField("signature", sig);
            stream.writeFormField("Content-Type", contentType);
            stream.writeFile("file", fileName, fileContents, XML_MIME_TYPE);
            stream.close();
            int code = conn.getResponseCode();
            if (code != REDIRECT_CODE && code != OK_CODE) {
                logger.log(Level.SEVERE,
                        "Server returned a bad code after upload: " + code);
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not send upload" + e.getMessage(),
                    e);
            return false;
        }
        return true;
    }

    /**
     * sends the zip file containing data/images to the server via an http upload
     * 
     * @param fileAbsolutePath
     */
    public static boolean upload(ByteArrayOutputStream outputStream,
            String fileName, String dir, String uploadUrl, String s3ID,
            String policy, String sig, String contentType,
            UploadStatusContainer uc) {

        return upload(outputStream.toByteArray(), fileName, dir, uploadUrl,
                s3ID, policy, sig, contentType, uc);
    }

    /**
     * uploads a byte array as a file to s3
     * 
     * @param fileData
     * @param fileName
     * @param dir
     * @param uploadUrl
     * @param s3ID
     * @param policy
     * @param sig
     * @param contentType
     * @param uc
     * @return
     */
    public static boolean upload(byte[] fileData, String fileName, String dir,
            String uploadUrl, String s3ID, String policy, String sig,
            String contentType, UploadStatusContainer uc) {
        try {
            HttpURLConnection conn = UploadUtil.createConnection(new URL(
                    uploadUrl));

            UploadUtil stream = new UploadUtil(conn.getOutputStream());
            stream.writeFormField("key", dir + "/${filename}");
            stream.writeFormField("AWSAccessKeyId", s3ID);
            stream.writeFormField("acl", "public-read");
            stream.writeFormField("success_action_redirect",
                    "http://www.gallatinsystems.com/SuccessUpload.html");
            stream.writeFormField("policy", policy);
            stream.writeFormField("signature", sig);
            stream.writeFormField("Content-Type", contentType);
            stream.writeFile("file", fileName, fileData, XML_MIME_TYPE);
            stream.close();
            int code = conn.getResponseCode();
            if (code != REDIRECT_CODE && code != OK_CODE) {
                logger.log(Level.SEVERE,
                        "Server returned a bad code after upload: " + code);
                if (uc != null) {
                    uc.setMessage("Server returned a bad code after upload: "
                            + code);
                }
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not send upload" + e.getMessage(),
                    e);
            if (uc != null) {
                uc.setMessage("Could not send upload: " + e.getMessage());
            }
            return false;
        }
        return true;
    }
}
