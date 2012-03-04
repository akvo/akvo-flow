package org.waterforpeople.mapping.app.web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.web.test.DataFixes;

public class QuestionAnswerStoreCleanup extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8740577368612948502L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DataFixes df = new DataFixes();
		df.fixQuestionAnswerStoreCollectionDate(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DataFixes df = new DataFixes();
		df.fixQuestionAnswerStoreCollectionDate(req, resp);
	}

}
