package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.adapter.SpreadsheetAccessPointAdapter;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.gdata.util.ServiceException;

public class SpreadsheetProcessorServlet extends AbstractRestApiServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2270875281089527752L;

	private static final Logger log = Logger
			.getLogger(SpreadsheetProcessorServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String spreadsheetName = req.getParameter("spreadsheetName");
		String listColumns = req.getParameter("listColumns");
		SpreadsheetAccessPointAdapter sapa = new SpreadsheetAccessPointAdapter();

		if (!spreadsheetName.trim().isEmpty()) {
			if (listColumns != null && listColumns.equals("true")) {
				try {
					StringBuilder sb = new StringBuilder();

					for (String item : sapa.listColumns(spreadsheetName)) {
						sb.append("column: " + item + "\n");
					}
					resp.getWriter().print(sb.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				sapa.processSpreadsheetOfAccessPoints(spreadsheetName);
				try {
					resp.getWriter().print("AccessPoints have been loaded");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {

	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// TODO Auto-generated method stub

	}

}
