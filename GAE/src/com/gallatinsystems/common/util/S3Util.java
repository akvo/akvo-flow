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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.gallatinsystems.common.Constants;

public class S3Util {

    private static final String S3_URL = "https://%s.s3.amazonaws.com/%s";
    private static final String GET_PAYLOAD = "GET\n\n\n%s\n/%s/%s";
    private static final String PUT_PAYLOAD = "PUT\n\n%s%s\n/%s/%s";

    public static URLConnection getConnection(String bucketName, String objectKey)
            throws IOException {

        final String date = getDate();
        final String payload = String.format(GET_PAYLOAD, date, bucketName, objectKey);

        final String awsAccessKeyId = PropertyUtil.getProperty("aws_identifier");
        final String awsAccessSecret = PropertyUtil.getProperty("aws_secret_key");

        final String signature = MD5Util.generateHMAC(payload, awsAccessSecret);

        final URL url = new URL(String.format(S3_URL, bucketName, objectKey));
        final URLConnection conn = url.openConnection();

        conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
        conn.setReadTimeout(Constants.CONNECTION_TIMEOUT);
        conn.setRequestProperty("Date", date);
        conn.setRequestProperty("Authorization", "AWS " + awsAccessKeyId + ":" + signature);

        return conn;
    }

    private static String getDate() {
        final DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date()) + "GMT";
    }

}
