package org.waterforpeople.mapping.dao;

import java.util.List;

import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.GeoRegion;

import com.gallatinsystems.framework.dao.BaseDAO;

@SuppressWarnings("unchecked")
public class GeoRegionDAO extends BaseDAO<GeoRegion> {

	public List<GeoRegion> list() {
		javax.jdo.Query query = pm.newQuery(GeoRegion.class);
		query.setOrdering("uuid, order asc");
		List<GeoRegion> region = (List<GeoRegion>) query.execute();
		return region;

	}

	public GeoRegion getGeoRegion(String key) {
		GeoRegion gr = null;
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		List<GeoRegion> region = (List<GeoRegion>) query.execute();
		if (region != null && region.size() > 0) {
			gr = region.get(0);
		}
		return gr;
	}

	public GeoRegionDAO() {
		super(GeoRegion.class);
	}
}
