package com.gallatinsystems.device.app.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import services.S3Driver;

import com.gallatinsystems.device.app.web.client.FieldVerifier;
import com.gallatinsystems.device.app.web.client.GreetingService;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.image.GAEImageAdapter;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	Logger log = Logger.getLogger(GreetingServiceImpl.class.getName());
	
	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");
//		return "Hello, " + input + "!<br><br>I am running " + serverInfo
//				+ ".<br><br>It looks like you are using:<br>" + userAgent;
//		
		SurveyDAO surveyDAO = new SurveyDAO();
		DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
		StringBuilder sb = new StringBuilder();
		for (DeviceSurveyJobQueue dsjq : dsjqDAO.get(input)) {
			sb.append(input + "," + dsjq.getSurveyID() + ","
					+ dsjq.getName() + "," + dsjq.getLanguage() + ",1.0\n");
		}
		return sb.toString();
	}
	
	public Boolean rotateImage(String fileName){
		String imageURL = fileName;
		String bucket = "waterforpeople";
		String rootURL = "http://waterforpeople.s3.amazonaws.com/";
		imageURL = "images/africa/malawi/" + imageURL;
		Random rand = new Random();
		String totalURL = rootURL + imageURL + "?random="+ rand.nextInt();
		InputStream in;
		ByteArrayOutputStream out = null;
		URL url;
		try {
			url = new URL(totalURL);
			in = url.openStream();
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int size;

			while ((size = in.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
		} catch (IOException e) {
			log.log(Level.SEVERE,"Could not rotate image",e);
		}

		GAEImageAdapter gaeImg = new GAEImageAdapter();
		byte[] newImage = gaeImg.rotateImage(out.toByteArray(), 90);
			S3Driver s3 = new S3Driver();
		String[] urlParts = imageURL.split("/");
		String imageName = urlParts[3];
		s3.uploadFile(bucket, imageURL, newImage);
		return null;

		/*
		 * resp.getWriter() .print( "<html><body><img src=\"" + totalURL +
		 * "\"/></body></html>");
		 */
		// serve the first image
		//resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		//resp.setContentType("image/jpeg");
		//resp.getOutputStream().write(newImage);
		 

	}
}
