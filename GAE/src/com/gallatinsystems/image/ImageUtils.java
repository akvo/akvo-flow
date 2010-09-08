package com.gallatinsystems.image;

import java.util.ArrayList;

public class ImageUtils {
	/*
	 * Utility method to return the parts of an image path for S3 
	 * Position 0 = web domain with bucket ends with / 
	 * Position 1 = middle path elements ends with / 
	 * Position 2 = file name
	 */
	public static String[] parseImageParts(String url) {
		String[] parts = new String[3];
		url = url.toLowerCase().replace("http://", "");
		String[] items = url.split("/");
		if (items.length == 3) {
			// no country in path
			parts[0]=("http://:" + items[0] + "/");
			parts[1]=(items[1] + "/");
			parts[2]=(items[2]);
		} else if (items.length > 3) {
			parts[0]=("http://:" + items[0] + "/");
			String middlePath = "";
			int i = 0;
			for (i = 1; i < items.length - 1; i++)
				middlePath += items[i] + "/";
			parts[1]=(middlePath);
			parts[2]=(items[i]);
		}

		return parts;
	}

	public static void main(String[] args) {
		String test1 = "http://waterforpeople.s3.amazonaws.com/images/wfpPhoto10062903227521.jpg";
		String test2 = "http://waterforpeople.s3.amazonaws.com/images/hn/ch003[1].jpg";
		System.out.println(test1);
		for (String item : parseImageParts(test1))
			System.out.println("   " + item);
		System.out.println(test2);
		for (String item : parseImageParts(test2))
			System.out.println("   " + item);
	}
}
