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

public final class FileUtil {
	public Boolean copyFile(String srcFile, String destFile) {
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

	private static final int BUFFER = 2048;

	public void generateKMZ(String inputFileName, String outputFileName) {
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
	
	public byte[] readFileBytes(File fx) throws IOException{
		FileInputStream fis;
		fis = new FileInputStream(fx);
		long length = fx.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = fis.read(bytes, offset, bytes.length
						- offset)) >= 0) {
			offset += numRead;
		}
		fis.close();
		return bytes;
	}

	public String writeToFile(String textToWrite, String fileName) {
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

	public String readFromFile(String fileName) throws IOException {
		StringBuilder sb = new StringBuilder();
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
	
	public static boolean createDir(String directory){
		File f = new File(directory);
		if(f.exists()){
			return true;
		}else{
			return f.mkdirs();
		}
	}
}

