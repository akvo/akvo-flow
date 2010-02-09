package com.gallatinsystems.image;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

public class GAEImageAdapter {
	
	public byte[] resizeImage(byte[] oldImageData, int width, int height){
		ImagesService imagesService = ImagesServiceFactory.getImagesService();

        Image oldImage = ImagesServiceFactory.makeImage(oldImageData);
        Transform resize = ImagesServiceFactory.makeResize(width, height);

        Image newImage = imagesService.applyTransform(resize, oldImage);

        byte[] newImageData = newImage.getImageData();		
        return newImageData;
	}

}
