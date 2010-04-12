package com.gallatinsystems.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	private final static int BUFFER = 2048;
	
	
	public static void main(String[] args){
		new ZipUtil().generateKMZ("<test xml></testxml>");
	}

	public ByteArrayOutputStream generateKMZ(String kmlContents) {
		ZipOutputStream zipOut = null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
//			bos.write(kmlContents.getBytes());
//
			zipOut = new ZipOutputStream(bos);
			zipOut.setLevel(ZipOutputStream.DEFLATED);
			ZipEntry entry = new ZipEntry("waterforpeoplemapping.kml");
			zipOut.putNextEntry(entry);
			zipOut.write(kmlContents.getBytes(), 0, kmlContents
					.getBytes().length);
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

}
