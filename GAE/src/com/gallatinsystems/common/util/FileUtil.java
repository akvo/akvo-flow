package com.gallatinsystems.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for manipulating files.
 * 
 * 
 */
public class FileUtil {
	private static final int BUFFER = 2048;

	/**
	 * copies a file to a new location on the local file system. This
	 * implementation will not create any directories (so the destination
	 * directory must exist).
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 */
	public static Boolean copyFile(String srcFile, String destFile) {
		Boolean copyFlag = false;
		File inputFile = new File(srcFile);
		File outputFile = new File(destFile);
		try {
			FileReader in = new FileReader(inputFile);
			FileWriter out = new FileWriter(outputFile);
			int c;

			while ((c = in.read()) != -1)
				out.write(c);

			in.close();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return copyFlag;
	}

	/**
	 * generates a kmz zip file
	 * 
	 * @param inputFileName
	 * @param outputFileName
	 * @deprecated
	 */
	public static void generateKMZ(String inputFileName, String outputFileName) {
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream("outputFileName");
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];
			// get a list of files from current directory
			File f = new File(inputFileName);
			// String files[] = f.list();

			// for (int i = 0; i < files.length; i++) {
			System.out.println("Adding: " + f.getName());
			FileInputStream fi = new FileInputStream(f);
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(f.getName());
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
			// }
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * reads the content of a File and returns a byte array representing the
	 * content.
	 * 
	 * @param fx
	 * @return the content of the file in a byte array
	 * @throws IOException
	 */
	public static byte[] readFileBytes(File fx) throws IOException {
		FileInputStream fis;
		fis = new FileInputStream(fx);
		long length = fx.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		fis.close();
		return bytes;
	}

	/**
	 * writes the contents of textToWrite to a file on the local filesystem
	 * identified by fileName.
	 * 
	 * @param textToWrite
	 * @param fileName
	 * @return - the absolute path of the file written
	 */
	public static String writeToFile(String textToWrite, String fileName) {
		File outFile = new File(fileName);
		try {
			FileWriter out = new FileWriter(outFile);
			out.write(textToWrite);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outFile.getAbsolutePath();

	}

	/**
	 * reads the contents of the file passed in and returns the content as a
	 * String. This assumes that the file is encoded in UTF-8
	 * 
	 * @param fileName
	 * @return - string representation of the file
	 * @throws IOException
	 */
	public static String readFromFile(String fileName) throws IOException {
		StringBuilder sb = new StringBuilder();
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in,
				"UTF-8"));
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	/**
	 * creates the directory passed in, creating any parents as needed.
	 * 
	 * @param directory
	 * @return - true if successful, false if not.
	 */
	public static boolean createDir(String directory) {
		File f = new File(directory);
		if (f.exists()) {
			return true;
		} else {
			return f.mkdirs();
		}
	}
}
