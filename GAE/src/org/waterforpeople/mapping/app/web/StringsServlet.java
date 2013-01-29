package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StringsServlet extends HttpServlet {

	private static final long serialVersionUID = -5814616069972956097L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		final InputStream is_strings = this.getClass().getClassLoader()
				.getResourceAsStream("locale/ui-strings.properties");
		final InputStream is_en = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("locale/en.properties");

		final Properties strings = new Properties();
		final Properties en = new Properties();

		strings.load(is_strings);
		en.load(is_en);

		System.out.println(en);
		System.out.println(strings);
	}
}
