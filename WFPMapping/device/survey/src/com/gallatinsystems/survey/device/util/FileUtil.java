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
import android.os.Environment;

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

	public static String getPathForFile(String file, String subDir,
			String useInternal) {
		String path = null;
		if (useInternal != null && "true".equalsIgnoreCase(useInternal)) {
			path = file;
		} else {
			String dir = getStorageDirectory(subDir, useInternal);
			path = dir + file;
		}
		return path;
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

	/**
	 * returns the path to the storage directory rooted at either the internal
	 * or external storage root. This method will NOT create any directories.
	 * 
	 * @param subDir
	 * @param useInternalStorage
	 * @return
	 */
	public static String getStorageDirectory(String subDir,
			String useInternalStorage) {
		return getStorageDirectory(subDir, null, useInternalStorage);
	}

	/**
	 * returns the full path to a storage directory rooted at either the
	 * internal or external storage root. If subdir is not null, it will be
	 * appended to the root. If fileName is not null, the last 5 characters of
	 * the filename (before the file extension, if any) will be used as
	 * directories. If the file name is < 5 characters long, it will be padded
	 * with 0
	 * 
	 * as an example: getStorageDirectory("test","photo12345.jpg", false) would
	 * return something like "/sdcard/test/1/2/3/4/5/" and
	 * getStorageDirectory("test,"
	 * abc.jpg",false) would return something like "/sdcard/test/a/b/c/0/0"
	 * 
	 * @param subDir
	 * @param fileName
	 * @param useInternalStorage
	 * @return
	 */
	public static String getStorageDirectory(String subDir, String fileName,
			String useInternalStorage) {
		String dir = "";
		if (useInternalStorage == null
				|| !"true".equalsIgnoreCase(useInternalStorage)) {
			dir = Environment.getExternalStorageDirectory().getAbsolutePath();

		} else {
			dir = Environment.getDataDirectory().getAbsolutePath();
		}
		if (!dir.endsWith(File.separator)) {
			dir += File.separator;
		}
		if (subDir != null) {
			dir += subDir;
		}
		if (fileName != null) {
			if (fileName.contains(".")) {
				fileName = fileName.substring(0, fileName.lastIndexOf("."));
			}
			char[] fileChars = fileName.toCharArray();
			int count = 0;
			for (int i = Math.max(0, fileChars.length - 5); i < fileChars.length; i++) {
				dir = dir + File.separator + fileChars[i];
				count++;
			}
			if (count < 5) {
				for (int i = count; i < 5; i++) {
					dir = dir + File.separator + "0";
				}
			}
		}
		return dir;
	}

	/**
	 * deletes all files in the directory (recursively) AND then deletes the
	 * directory itself if the "deleteFlag" is true
	 * 
	 * @param dir
	 */
	public static void deleteFilesInDirectory(File dir, boolean deleteDir) {
		if (dir != null && dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						files[i].delete();
					} else {
						// recursively delete
						deleteFilesInDirectory(files[i], true);
					}
				}
			}
			// now delete the directory itself
			if (deleteDir) {
				dir.delete();
			}
		}
	}

	/**
	 * non-recursive delete of all files in a single directory that match the
	 * expression (regex) passed in
	 * 
	 * @param path
	 * @param expression
	 */
	public static void deleteFilesMatchingExpression(String path,
			String expression) {
		deleteFilesMatchingExpression(new File(path), expression, false);
	}

	/**
	 * non-recursive delete of all files in a single directory that match the
	 * expression (regex) passed in
	 * 
	 * @param path
	 * @param expression
	 */
	public static void deleteFilesMatchingExpression(String path,
			String expression, boolean recurse) {
		deleteFilesMatchingExpression(new File(path), expression, recurse);
	}

	/**
	 * delete of all files in a single directory that match the expression
	 * (regex) passed in
	 * 
	 * @param path
	 * @param expression
	 * @param recurse
	 *            - if true, traverse subdirs too
	 */
	public static void deleteFilesMatchingExpression(File root,
			String expression, boolean recurse) {
		if (root != null) {
			if (root.isDirectory()) {
				File[] files = root.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						if (recurse && files[i].isDirectory()) {
							deleteFilesMatchingExpression(files[i], expression,
									recurse);
						} else if (files[i].isFile()) {
							if (files[i].getName().matches(expression)) {
								files[i].delete();
							}
						}
					}
				}
			}
		}
	}

}
