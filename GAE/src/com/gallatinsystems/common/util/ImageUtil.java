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
 * 
 * 
 */
public class ImageUtil {

	public static File resizeImage(File image, String destImageDir,
			Integer width, Integer height) {
		String destFile = destImageDir + File.separator + image.getName();
		File destFileObj = new File(destFile);
		if (image.getAbsolutePath().toLowerCase().contains(".jpg")) {
			if (image != null && image.length() > 0) {
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
