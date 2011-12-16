package com.gallatinsystems.image;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

/**
 * 
 * Adapter to use the GAE image api to manipulate images
 *
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
		ImagesService imagesService2= ImagesServiceFactory.getImagesService();
		Image oldImage = ImagesServiceFactory.makeImage(oldImageData);
		Transform resize = ImagesServiceFactory.makeResize(width, height);
		
		Image newImage = imagesService2.applyTransform(resize, oldImage);

		byte[] newImageData = newImage.getImageData();
		return newImageData;
	}

	/**
	 * rotates an image
	 * @param image
	 * @param degrees
	 * @return
	 */
	public byte[] rotateImage(byte[] image, Integer degrees) {
		Image oldImage = ImagesServiceFactory.makeImage(image);		
		Image newImage = imagesService.applyTransform(ImagesServiceFactory.makeRotate(degrees), oldImage);
		

		byte[] newImageData = newImage.getImageData();
		return newImageData;
	}

	public enum DIRECTION {
		LEFT, RIGHT
	};

}
