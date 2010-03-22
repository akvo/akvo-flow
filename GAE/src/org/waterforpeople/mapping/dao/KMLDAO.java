package org.waterforpeople.mapping.dao;

import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.KML;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.google.appengine.api.datastore.Text;

public class KMLDAO extends BaseDAO<KML> {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(KMLDAO.class.getName());

	public KMLDAO() {
		super(KML.class);
	}

	public String saveKML(String kmlText) {
		KML kml = new KML();
		kml.setKmlText(new Text(kmlText));
		return save(kml).getKey().getId()+"";
	}
	
	public String getKML(String key) {
		KML kml = getByKey(key);
		if (kml != null) {
			return kml.getKmlText().toString();
		} else {
			return "";
		}
	}
}
