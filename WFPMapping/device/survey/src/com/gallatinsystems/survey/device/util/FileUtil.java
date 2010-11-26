package com.gallatinsystems.survey.device.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.ZipInputStream;

import android.content.Context;

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
	public static void writeStringToFile(String contents,
			FileOutputStream filePath) throws IOException {
		if (contents != null) {
			BufferedOutputStream bw = new BufferedOutputStream(filePath);
			bw.write(contents.getBytes("UTF-8"));
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
	public static boolean doesFileExist(String file, String subDir,
			String useInternal, Context c) {
		boolean exists = false;
		if (file != null) {
			FileInputStream in = null;
			try {
				in = getFileInputStream(file, subDir, useInternal, c);
				exists = true;
				in.close();
			} catch (FileNotFoundException e) {
				exists = false;
			} catch (IOException e) {
				// no-op
			}
		}
		return exists;
	}


	/**
	 * reads the contents of a file into a string.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFileAsString(String file) throws IOException {
		StringBuilder contents = new StringBuilder();
		BufferedReader input = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = input.readLine()) != null) {
			contents.append(line);
			contents.append(System.getProperty("line.separator"));
		}
		input.close();
		return contents.toString();
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
			FileOutputStream destinationFile) throws IOException {		
		ByteArrayOutputStream out = readZipEntry(zip);
		destinationFile.write(out.toByteArray());
		out.close();
		destinationFile.close();
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

	public static FileOutputStream getFileOutputStream(String file,
			String subDir, String useInternal, Context c)
			throws FileNotFoundException {
		FileOutputStream out = null;
		if (useInternal != null && "true".equalsIgnoreCase(useInternal)
				&& c != null) {
			out = c.openFileOutput(file, Context.MODE_WORLD_WRITEABLE);
		} else {
			String dir = getStorageDirectory(subDir, useInternal);
			findOrCreateDir(dir);
			out = new FileOutputStream(dir + file);
		}
		return out;
	}

	public static FileInputStream getFileInputStream(String file,
			String subDir, String useInternal, Context c)
			throws FileNotFoundException {
		FileInputStream in = null;
		if (useInternal != null && "true".equalsIgnoreCase(useInternal)) {
			in = c.openFileInput(file);
		} else {
			String dir = getStorageDirectory(subDir, useInternal);
			in = new FileInputStream(dir + file);
		}
		return in;
	}

	public static String getStorageDirectory(String subDir,
			String useInternalStorage) {
		String dir = "";
		if (useInternalStorage == null
				|| "false".equalsIgnoreCase(useInternalStorage)) {
			dir = ConstantUtil.SD_CARD_ROOT;
			if (subDir != null) {
				dir += subDir;
			}
		}
		return dir;
	}

}
