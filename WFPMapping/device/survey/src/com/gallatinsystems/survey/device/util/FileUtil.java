package com.gallatinsystems.survey.device.util;

import java.io.File;

/**
 * utility for manipulating files
 * 
 * @author Christopher Fagiani
 * 
 */
public class FileUtil {

	/**
	 * creates the data directory if it does not exist
	 */
	public static void findOrCreateDir(String directory) {
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static boolean doesFileExist(String file) {
		File f = new File(file);
		return f.exists();
	}

	public static String convertRemoteToLocalFile(String remoteFile,
			String surveyId) {
		String localDir = ConstantUtil.DATA_DIR;
		if (surveyId != null) {
			localDir += surveyId + "/";
		}
		FileUtil.findOrCreateDir(localDir);
		return localDir + remoteFile.substring(remoteFile.lastIndexOf("/") + 1);
	}
}
