package org.waterforpeople.mapping.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.GeoRegion;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

@SuppressWarnings("unchecked")
public class GeoRegionDAO extends BaseDAO<GeoRegion> {

	public List<GeoRegion> list() {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<GeoRegion> region = null;
		javax.jdo.Query query = pm.newQuery(GeoRegion.class);
		query.setOrdering("uuid, order asc");
		region = (List<GeoRegion>) query.execute();
		return region;
	}

	public GeoRegionDAO() {
		super(GeoRegion.class);
	}
}
