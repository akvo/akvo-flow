package org.waterforpeople.mapping.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dao.GeoRegionDAO;
import org.waterforpeople.mapping.domain.GeoRegion;

import com.google.appengine.api.datastore.Key;

public class GeoRegionHelper {
	private static final Logger log = Logger.getLogger(GeoRegionHelper.class
			.getName());

	public ArrayList<GeoRegion> processRegionsSurvey(
			ArrayList<String> regionLines) {
		ArrayList<GeoRegion> geoRegions = null;
		GeoRegionDAO grDAO = new GeoRegionDAO();
		int iCurrRegion = 1;
		int iCurrRow = 1;
		UUID currentUUID = UUID.randomUUID();
		for (String s : regionLines) {
			GeoRegion gr = new GeoRegion();
			String[] contents = s.split(",");

			Integer iLineRegion = new Integer(contents[0]);

			if (iCurrRow == 1) {
				iCurrRegion = iLineRegion;
			} else if (iCurrRegion != iLineRegion) {
				iCurrRegion = iLineRegion;
				currentUUID = UUID.randomUUID();
			}
			gr.setUuid(currentUUID.toString());
			gr.setOrder(new Long(contents[1]));
			gr.setName(contents[2]);
			gr.setLatitiude(new Double(contents[3]));
			gr.setLongitude(new Double(contents[4]));
			// gr.setCreateDateTime(new Date(contents[5]));
			Key key = grDAO.save(gr);
			iCurrRow++;
			log.info("Saved RegionRow: " + key.toString());
		}
		return geoRegions;
	}

}
