package com.gallatinsystems.common.util;

import java.util.List;

import services.S3Driver;

public class DeviceFileUploadCompare {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new DeviceFileUploadCompare().compare(args[0],args[1]);
	}
	
	private void compare(String key, String identifier){
		S3Driver s3 = new S3Driver(key,identifier);
		String bucketName ="waterforpeople";
		String fileDir = "devicezip";
		String fileListPath = null;
		
		List<String> fileList = s3.listAllFiles(bucketName, fileDir,
				fileListPath);
	}

}
