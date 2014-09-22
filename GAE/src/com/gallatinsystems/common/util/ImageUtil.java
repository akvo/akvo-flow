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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

/**
 * Utility for resizing images
 */
public class ImageUtil {

    /**
     * generates a new image in the destImageDir with the same file name as the image file passed
     * in. NOTE: you cannot use this method to overwrite the source image (i.e. you cannot specify
     * the destDir as the same directory that houses the source image)
     * 
     * @param image
     * @param destImageDir
     * @param width
     * @param height
     * @return
     */
    public static File resizeImage(File image, String destImageDir,
            Integer width, Integer height) {
        String destFile = destImageDir + File.separator + image.getName();
        File destFileObj = new File(destFile);
        if (image.getAbsolutePath().toLowerCase().contains(".jpg")) {
            if (image.length() > 0) {
                if (!destFileObj.exists()) {
                    InputStream input;
                    try {
                        input = new FileInputStream(image);
                        InputStream resizedImage = scaleImage(input, width,
                                height);
                        int data = resizedImage.read();

                        FileOutputStream output = new FileOutputStream(destFile);
                        while (data != -1) {
                            output.write(data);
                            data = resizedImage.read();
                        }
                        output.close();
                        input.close();
                        input = null;
                        resizedImage.close();
                        resizedImage = null;

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("  File already exists: " + destFile);
                }
            }
        }
        return destFileObj;
    }

    /**
     * resizes the image represented by the inputstream passed in into a new InputStream. The resize
     * will attempt to maintain the aspect ratio of the orginal image.
     * 
     * @param p_image
     * @param p_width
     * @param p_height
     * @return
     * @throws Exception
     */
    public static InputStream scaleImage(InputStream p_image, int p_width,
            int p_height) throws Exception {

        BufferedImage src = ImageIO.read(p_image);
        int thumbWidth = p_width;
        int thumbHeight = p_height;

        // Make sure the aspect ratio is maintained, so the image is not skewed
        double thumbRatio = (double) thumbWidth / (double) thumbHeight;
        int imageWidth = src.getWidth(null);
        int imageHeight = src.getHeight(null);
        double imageRatio = (double) imageWidth / (double) imageHeight;
        if (thumbRatio < imageRatio) {
            thumbHeight = (int) (thumbWidth / imageRatio);
        } else {
            thumbWidth = (int) (thumbHeight * imageRatio);
        }

        BufferedImage dest = new BufferedImage(thumbWidth, thumbHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(
                (double) thumbWidth / src.getWidth(), (double) thumbHeight
                        / src.getHeight());
        g.drawRenderedImage(src, at);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        ImageIO.write(dest, "JPG", ios);
        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());

        return bis;
    }

}
