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

package org.waterforpeople.mapping.app.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import services.S3Driver;

public class PhotoUpload extends HttpServlet {

    private static final long serialVersionUID = 4496360086104690603L;
    private static final Logger log = Logger.getLogger(PhotoUpload.class
            .getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {

    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Properties props = System.getProperties();

        String bucket = props.getProperty("s3bucket");
        ServletFileUpload upload = new ServletFileUpload();
        try {
            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream in = item.openStream();
                ByteArrayOutputStream out = null;
                try {

                    in = item.openStream();
                    out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8096];
                    int size;

                    while ((size = in.read(buffer, 0, buffer.length)) != -1) {
                        out.write(buffer, 0, size);
                    }
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Could not rotate image", e);
                }
                S3Driver s3 = new S3Driver();
                s3.uploadFile(bucket, "images/" + item.getName(), out
                        .toByteArray());
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not save image", e);
        }

    }

}
