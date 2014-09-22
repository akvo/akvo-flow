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
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.S3Driver;

import com.gallatinsystems.image.GAEImageAdapter;

public class ImageProcessorServlet extends HttpServlet {

    private static final long serialVersionUID = 2781690180514475579L;
    private static final Logger log = Logger
            .getLogger(ImageProcessorServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        rotateImage(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        rotateImage(req, resp);
    }

    private void rotateImage(HttpServletRequest req, HttpServletResponse resp) {
        String imageURL = req.getParameter("imageURL");
        Integer degrees = 0;
        if (req.getParameter("degrees") != null) {
            degrees = new Integer(req.getParameter("degrees"));
        } else {
            degrees = 90;
        }

        String rootURL = "http://waterforpeople.s3.amazonaws.com/";
        imageURL = "images/africa/malawi/" + imageURL;
        Random rand = new Random();

        String totalURL = rootURL + imageURL + "?random=" + rand.nextInt();
        ;
        URL url;
        InputStream in;
        ByteArrayOutputStream out = null;
        try {
            url = new URL(totalURL);
            in = url.openStream();
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int size;

            while ((size = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }

            log.info("Before size: " + out.size());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not rotate image", e);
        }

        GAEImageAdapter gaeImg = new GAEImageAdapter();
        byte[] newImage = gaeImg.rotateImage(out.toByteArray(), degrees);
        log.info("After size: " + newImage.length);
        // S3Driver s3 = new S3Driver();
        // String[] urlParts = imageURL.split("/");
        // String imageName = urlParts[3];
        // s3.uploadFile(bucket, imageURL, newImage);

        /*
         * resp.getWriter() .print( "<html><body><img src=\"" + totalURL + "\"/></body></html>");
         */
        // serve the first image
        // resp.setHeader("Cache-Control",
        // "no-store, no-cache, must-revalidate");
        // resp.setContentType("image/jpeg");
        // try {
        // resp.getOutputStream().write(newImage);
        // } catch (IOException e1) {
        // e1.printStackTrace();
        // }
        String contextPath = req.getContextPath();

        try {
            resp.sendRedirect(resp.encodeRedirectURL(contextPath
                    + "/MalawiPhotos.html"));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not rotate image", e);
        }

    }

    @SuppressWarnings("unused")
    private void resizeImage(HttpServletRequest req, HttpServletResponse resp) {
        String imageURL = req.getParameter("imageURL");
        imageURL = "http://dru-test.s3.amazonaws.com/" + imageURL;
        URL url;
        InputStream in;
        ByteArrayOutputStream out = null;
        try {
            url = new URL(imageURL);
            in = url.openStream();
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int size;

            while ((size = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
            resp.getWriter().println("Before size: " + out.size());
            log.info("Before size: " + out.size());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not resize image", e);
        }

        GAEImageAdapter gaeImg = new GAEImageAdapter();
        byte[] newImage = gaeImg.resizeImage(out.toByteArray(), 500, 500);
        log.info("After size: " + newImage.length);
        S3Driver s3 = new S3Driver();
        String[] urlParts = imageURL.split("/");
        String imageName = urlParts[3];
        s3.uploadFile("resizedimages", imageName, newImage);
        try {
            resp.getWriter().println("After size: " + newImage.length);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not resize image", e);
        }
        resp.setContentType("text/plain");

    }
}
