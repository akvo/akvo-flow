package org.waterforpeople.mapping.app.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.S3Driver;

import com.gallatinsystems.image.GAEImageAdapter;

public class ImageProcessorServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(ImageProcessorServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		resizeImage(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		resizeImage(req, resp);
	}

	private void resizeImage(HttpServletRequest req, HttpServletResponse resp) {
		String imageURL = req.getParameter("imageURL");
		imageURL = "http://dru-test.s3.amazonaws.com/" + imageURL;
		URL url;
		InputStream in;
		ByteArrayOutputStream out = null;
		try {
			url = new URL(imageURL);
			in = url.openStream();
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int size;

			while ((size = in.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
			resp.getWriter().println("Before size: " + out.size());
			log.info("Before size: " + out.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GAEImageAdapter gaeImg = new GAEImageAdapter();
		byte[] newImage = gaeImg.resizeImage(out.toByteArray(), 300, 300);
		log.info("After size: " + newImage.length);
		S3Driver s3 = new S3Driver();
		String[] urlParts = imageURL.split("/");
		String imageName = urlParts[3];
		s3.uploadFile("resizedimages", imageName, newImage);
		try {
			resp.getWriter().println("After size: " + newImage.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resp.setContentType("text/plain");

	}
}
