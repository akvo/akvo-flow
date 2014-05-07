/*
 *  Copyright (C) 2013 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */
package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.web.rest.security.AppRole;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.geography.domain.Country;

public class EnvServlet extends HttpServlet {

	private static final long serialVersionUID = 7830536065252808839L;
	private static final Logger log = Logger.getLogger(EnvServlet.class
			.getName());

	private static final ArrayList<String> properties = new ArrayList<String>();

	static {
		properties.add("photo_url_root");
		properties.add("imageroot");
		properties.add("flowServices");
		properties.add("surveyuploadurl");
		properties.add("showStatisticsFeature");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		final VelocityEngine engine = new VelocityEngine();
		engine.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.NullLogChute");
		try {
			engine.init();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not initialize velocity", e);
		}

		Template t = null;
		try {
			t = engine.getTemplate("Env.vm");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not get the template `CurrentUser`", e);
			return;
		}

		final VelocityContext context = new VelocityContext();
		final Map<String, String> props = PropertyUtil
				.getPropertiesMap(properties);

		// if the showStatisticsFeature is not present in appengine-web.xml, we want it to be false.
		if (props.get("showStatisticsFeature") == null) props.put("showStatisticsFeature", "false");

		final BaseDAO<Country> countryDAO = new BaseDAO<Country>(Country.class);
		final JSONArray jsonArray = new JSONArray();
		for (Country c : countryDAO.list(Constants.ALL_RESULTS)) {
			if (c.getIncludeInExternal() != null
					&& c.getIncludeInExternal()
					&& (c.getCentroidLat().equals(0d) || c.getCentroidLon()
							.equals(0d))) {
				log.log(Level.SEVERE,
						"Country "
								+ c.getIsoAlpha2Code()
								+ " was configured to show in the map, but doesn't have proper centroids");
				continue;
			}
			if (c.getIncludeInExternal() != null && c.getIncludeInExternal()) {
				jsonArray.put(new JSONObject(c));
			}
		}
		props.put("countries", jsonArray.toString());

		context.put("env", props);

		final List<Map<String, String>> roles = new ArrayList<Map<String, String>>();
		for (AppRole r : AppRole.values()) {
			if (r.getLevel() < 10) {
				continue; // don't expose NEW_USER, nor SUPER_USER
			}
			Map<String, String> role = new HashMap<String, String>();
			role.put("value", String.valueOf(r.getLevel()));
			role.put("label", "_" + r.toString());
			roles.add(role);
		}
		context.put("roles", roles);

		final StringWriter writer = new StringWriter();
		t.merge(context, writer);

		resp.setContentType("application/javascript;charset=UTF-8");

		final PrintWriter pw = resp.getWriter();
		pw.println(writer.toString());
		pw.close();
	}
}
