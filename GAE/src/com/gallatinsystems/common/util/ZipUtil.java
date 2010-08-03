package com.gallatinsystems.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	public static void main(String[] args) {
		generateZip("<test xml></testxml>");
	}

	public static ByteArrayOutputStream generateZip(String kmlContents) {
		ZipOutputStream zipOut = null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			zipOut = new ZipOutputStream(bos);
			zipOut.setLevel(ZipOutputStream.DEFLATED);
			ZipEntry entry = new ZipEntry("waterforpeoplemapping.kml");
			zipOut.putNextEntry(entry);
			zipOut.write(kmlContents.getBytes());
			zipOut.closeEntry();
			zipOut.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bos;

	}

	public static String unZip(byte[] contents) throws IOException {
		ByteArrayInputStream zipContents = new ByteArrayInputStream(contents);
		ZipInputStream zis = new ZipInputStream(zipContents);
		ZipEntry entry;
		StringBuilder line = new StringBuilder();
		while ((entry = zis.getNextEntry()) != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int size;
			while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
			line.append(out.toString());

			out.close();
		}
		zis.closeEntry();

		return line.toString();
	}

}
