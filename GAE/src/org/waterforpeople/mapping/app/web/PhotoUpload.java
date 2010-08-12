package org.waterforpeople.mapping.app.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import services.S3Driver;

public class PhotoUpload extends HttpServlet {

	private static final long serialVersionUID = 4496360086104690603L;
	private static final Logger log = Logger.getLogger(PhotoUpload.class
			.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		Properties props = System.getProperties();

		String bucket = props.getProperty("s3bucket");
		ServletFileUpload upload = new ServletFileUpload();
		try {
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();			
				InputStream in = item.openStream();
				ByteArrayOutputStream out = null;				
				try {

					in =  item.openStream();
					out = new ByteArrayOutputStream();
					byte[] buffer = new byte[8096];
					int size;

					while ((size = in.read(buffer, 0, buffer.length)) != -1) {
						out.write(buffer, 0, size);
					}
				} catch (IOException e) {
					log.log(Level.SEVERE, "Could not rotate image", e);
				}
				S3Driver s3 = new S3Driver();
				s3.uploadFile(bucket, "images/"+item.getName(), out.toByteArray());
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
