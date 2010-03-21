package org.waterforpeople.mapping.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.KML;

import com.google.appengine.api.datastore.Text;

public class KMLDAO {
	private static final Logger log = Logger.getLogger(KMLDAO.class
			.getName());

	PersistenceManager pm;

	private void init() {
		pm = PMF.get().getPersistenceManager();
	}

	public KMLDAO() {
		init();
	}
	
	public Long saveKML(String kmlText) {
		KML kml = new KML();
		kml.setKmlText(new Text(kmlText));
		kml.setCreateDateTime(new Date());
		pm.makePersistent(kml);
		return kml.getId();
	}

	public String getKML(Long id) {
		KML kml = null;

		javax.jdo.Query query = pm.newQuery(KML.class);
		query.setFilter("id == idParam");
		query.declareParameters("Long idParam");
		List<KML> results = (List<KML>) query.execute(id);
		if (results.size() > 0) {
			kml = results.get(0);
		}
		return kml.getKmlText().toString();
	}
}
