package com.gallatinsystems.survey.device.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipInputStream;

/**
 * utility for manipulating files
 * 
 * @author Christopher Fagiani
 * 
 */
public class FileUtil {

	private static final int BUFFER_SIZE = 2048;

	/**
	 * writes the contents string to the file indicated by filePath
	 * 
	 * @param contents
	 * @param filePath
	 * @throws IOException
	 */
	public static void writeStringToFile(String contents, String filePath)
			throws IOException {
		if (filePath.contains(File.separator)) {
			String dir = filePath.substring(0, filePath
					.lastIndexOf(File.separator));
			if (dir != null && dir.trim().length() > 0) {
				findOrCreateDir(dir);
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			bw.write(contents);
			bw.flush();
			bw.close();
		}
	}

	/**
	 * creates the data directory if it does not exist. If the directory already
	 * existed, the File object representing that directory will be returned,
	 * otherwise this will return null.
	 */
	public static File findOrCreateDir(String directory) {
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs();
			return null;
		}
		return dir;
	}

	/**
	 * checks if a file exists
	 * 
	 * @param file
	 * @return
	 */
	public static boolean doesFileExist(String file) {
		File f = new File(file);
		return f.exists();
	}

	/**
	 * parses out everything except the file name (no path) from the remoteFile
	 * and returns a fully qualified path to where this file is stored in the
	 * data directory.
	 * 
	 * @param remoteFile
	 * @param surveyId
	 * @return
	 */
	public static String convertRemoteToLocalFile(String remoteFile,
			String surveyId) {
		String localDir = ConstantUtil.DATA_DIR;
		if (surveyId != null) {
			localDir += surveyId + "/";
		}
		FileUtil.findOrCreateDir(localDir);
		return localDir + remoteFile.substring(remoteFile.lastIndexOf("/") + 1);
	}

	/**
	 * reads data from a zipInputStream into a string. The ZipInputStream must
	 * already be positioned at the correct ZipEntry prior to invoking this
	 * method.
	 * 
	 * @param zis
	 * @return
	 * @throws IOException
	 */
	public static String readTextFromZip(ZipInputStream zis) throws IOException {
		ByteArrayOutputStream out = readZipEntry(zis);
		String data = out.toString();
		out.close();
		return data;
	}

	/**
	 * reads binary data from a zipInputSream and saves it to the
	 * destinationFile passed in. The ZipInputStream must already be positioned
	 * at the ZipEntry for the file to be saved.
	 * 
	 * @param zip
	 * @param destinationFile
	 * @throws IOException
	 */
	public static void extractAndSaveFile(ZipInputStream zip,
			String destinationFile) throws IOException {
		if (destinationFile.contains(File.separator)) {
			String dirOnly = destinationFile.substring(0, destinationFile
					.lastIndexOf(File.separator));
			if (dirOnly != null && dirOnly.trim().length() > 0) {
				findOrCreateDir(dirOnly);
			}
		}

		FileOutputStream fis = new FileOutputStream(destinationFile);
		ByteArrayOutputStream out = readZipEntry(zip);
		fis.write(out.toByteArray());
		out.close();
		fis.close();
	}

	/**
	 * reads the contents of a ZipEntry into a ByteArrayOutputStream. The
	 * ZipInputStream passed in must be positioned at the desired ZipEntry prior
	 * to being passed to this method
	 * 
	 * @param zis
	 * @return
	 * @throws IOException
	 */
	public static ByteArrayOutputStream readZipEntry(ZipInputStream zis)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUFFER_SIZE];
		int size;
		while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
			out.write(buffer, 0, size);
		}
		return out;
	}
}
