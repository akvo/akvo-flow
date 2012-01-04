package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.google.appengine.api.datastore.Entity;

public class ScoreProcessor extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5500271297082259592L;

	private static final Logger log = Logger.getLogger(ScoreProcessor.class
			.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		AccessPointDao apDao = new AccessPointDao();
		Iterable<Entity> keys = apDao.listRawEntity(true);
		AccessPointHelper aph = new AccessPointHelper();
		for (Entity item : keys) {
			try {
				AccessPoint apItem = apDao.findByKey(item.getKey());
				aph.scoreAccessPointNew(apItem);
			} catch (Exception ex) {
				log.info("Swallowing exception because of old objects on fetch AP:"
						+ item.getKey().toString());
			}
		}
	}
}
