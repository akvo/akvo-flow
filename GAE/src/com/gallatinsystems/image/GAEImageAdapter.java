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

package com.gallatinsystems.image;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

/**
 * Adapter to use the GAE image api to manipulate images
 */
public class GAEImageAdapter {
    private ImagesService imagesService = null;

    public GAEImageAdapter() {
        init();
    }

    private void init() {
        imagesService = ImagesServiceFactory.getImagesService();

    }

    /**
     * resizes an image
     * 
     * @param oldImageData
     * @param width
     * @param height
     * @return
     */
    public byte[] resizeImage(byte[] oldImageData, int width, int height) {
        ImagesService imagesService2 = ImagesServiceFactory.getImagesService();
        Image oldImage = ImagesServiceFactory.makeImage(oldImageData);
        Transform resize = ImagesServiceFactory.makeResize(width, height);

        Image newImage = imagesService2.applyTransform(resize, oldImage);

        byte[] newImageData = newImage.getImageData();
        return newImageData;
    }

    /**
     * rotates an image
     * 
     * @param image
     * @param degrees
     * @return
     */
    public byte[] rotateImage(byte[] image, Integer degrees) {
        Image oldImage = ImagesServiceFactory.makeImage(image);
        Image newImage = imagesService.applyTransform(ImagesServiceFactory.makeRotate(degrees),
                oldImage);

        byte[] newImageData = newImage.getImageData();
        return newImageData;
    }

    public enum DIRECTION {
        LEFT, RIGHT
    };

}
