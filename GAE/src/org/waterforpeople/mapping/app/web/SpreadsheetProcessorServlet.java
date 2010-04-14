package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.adapter.SpreadsheetAccessPointAdapter;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;
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
		SpreadsheetAccessPointAdapter sapa = new SpreadsheetAccessPointAdapter(null,null);
		String clearAccessPointFlag = req.getParameter("clearAccessPointFlag");
		
		if(clearAccessPointFlag.equals("doIt")){
			BaseDAO<AccessPoint> baseDAO = new BaseDAO<AccessPoint>(AccessPoint.class);
			List<AccessPoint> apList = baseDAO.list(Constants.ALL_RESULTS);
			for(AccessPoint item: apList){
				try {
					resp.getWriter().println("Deleting: " + item.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				baseDAO.delete(item);
			}
			
			try {
				resp.getWriter().println("FINISHED ACCESSPOINT TABLE SHOULD BE EMPTY");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (!spreadsheetName.trim().isEmpty()) {
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
				try {
					sapa.processSpreadsheetOfAccessPoints(spreadsheetName);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ServiceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
