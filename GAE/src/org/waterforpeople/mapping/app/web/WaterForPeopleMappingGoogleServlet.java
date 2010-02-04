package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.DeviceFiles;

import com.google.appengine.repackaged.com.google.common.base.Log;

@SuppressWarnings("serial")
public class WaterForPeopleMappingGoogleServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(WaterForPeopleMappingGoogleServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		String showKML = req.getParameter("showKML");
		String processFile = req.getParameter("processFile");
		String listFiles = req.getParameter("listFiles");
		String testVelocity = req.getParameter("testVelocity");
		if (showKML != null) {
			StringBuilder sb = new StringBuilder();
			System.out.println("Show KML");

			javax.jdo.Query query = pm.newQuery("select from  "
					+ AccessPoint.class.getName());
			List<AccessPoint> entries = (List<AccessPoint>) query.execute();
			sb
					.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <kml xmlns=\"http://earth.google.com/kml/2.1\"> ");
			sb
					.append("<Document> <name>a name for this list</name><description>a useful description</description>");
			for (AccessPoint ap : entries) {
				sb.append("<Placemark>");
				sb.append("<name>" + ap.getCommunityCode() + "</name>");
				sb.append("<Point><coordinates>" + ap.getLatitude() + ","
						+ ap.getLongitude() + "," + ap.getAltitude()
						+ "</coordinates></Point>");
				sb.append("</Placemark>");
			}
			pm.close();
			sb.append("</Document>");
			sb.append("</kml>");
			System.out.println(sb.toString());
			resp.setContentType("application/vnd.google-earth.kml+xml");
			resp.getWriter().println(sb.toString());
		} else if (listFiles != null) {
			// List processed files
			javax.jdo.Query query = pm.newQuery("select from  "
					+ DeviceFiles.class.getName());
			List<DeviceFiles> entries = (List<DeviceFiles>) query.execute();
			StringBuilder sb = new StringBuilder();
			for (DeviceFiles df : entries) {
				sb.append(df.toString());
			}
			pm.close();

			resp.setContentType("text/plain");
			resp.getWriter().println(sb.toString());

		} else if (testVelocity != null) {
			KMLGenerator kmlGen = new KMLGenerator();
			String placemarksDocument = kmlGen
					.generateDocument("PlacemarkTabs.vm");
			resp.setContentType("text/plain");
			resp.getWriter().println(placemarksDocument);
		} else {
			javax.jdo.Query query = pm.newQuery("select from  "
					+ AccessPoint.class.getName());
			List<AccessPoint> entries = (List<AccessPoint>) query.execute();
			StringBuilder sb = new StringBuilder();
			for (AccessPoint ap : entries) {
				sb.append(ap.toString());
			}
			pm.close();

			resp.setContentType("text/plain");
			resp.getWriter().println(sb.toString());
		}
	}

}
