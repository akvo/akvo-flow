package com.gallatinsystems.gis.map.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.gallatinsystems.gis.map.domain.OGRFeature.FeatureType;

public class OGRFeatureDao extends BaseDAO<OGRFeature> {

	public OGRFeatureDao() {
		super(OGRFeature.class);

	}

	public List<OGRFeature> listByExtentAndType(Double x1, Double y1,
			OGRFeature.FeatureType featureType, String orderByCol,
			String orderByDirection, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(OGRFeature.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("x1", filterString, paramString, "Double", x1,
				paramMap, LTE_OP);
		appendNonNullParam("featureType", filterString, paramString, "String",
				featureType, paramMap, EQ_OP);

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		if (orderByCol != null && orderByDirection != null)
			query.setOrdering(orderByCol + " " + orderByDirection);

		prepareCursor(cursorString, query);
		@SuppressWarnings("unchecked")
		List<OGRFeature> resultsGTE = (List<OGRFeature>) query
				.executeWithMap(paramMap);
		List<OGRFeature> results = new ArrayList<OGRFeature>();
		for (OGRFeature item : resultsGTE) {
			Double[] boundingBox = item.getBoundingBox();
			if (boundingBox[3] >y1) {
				results.add(item);
			}
		}

		return results;
	}

	public OGRFeature findByCountryAndType(String countryCode,
			FeatureType featureType) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(OGRFeature.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("featureType", filterString, paramString, "String",
				featureType, paramMap, EQ_OP);
		appendNonNullParam("countryCode", filterString, paramString, "String",
				countryCode, paramMap, EQ_OP);

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		@SuppressWarnings("unchecked")
		List<OGRFeature> results = (List<OGRFeature>) query
				.executeWithMap(paramMap);
		if (results != null && results.size() > 0)
			return results.get(0);
		else
			return null;
	}

	public OGRFeature save(OGRFeature item) {
		// If type == country then must update can't have 2 shapes for 1 country
		if (item.getFeatureType().equals(FeatureType.COUNTRY)) {
			OGRFeature existingItem = findByCountryAndType(
					item.getCountryCode(), FeatureType.COUNTRY);
			if (existingItem != null) {
				existingItem.setGeometry(item.getGeometry());
				existingItem.setBoundingBox(item.getBoundingBox());
				super.save(existingItem);
				return existingItem;
			} else {
				super.save(item);
				return item;
			}
		} else {
			// Need to think about this branch probably need to look up existing
			// features and update
			super.save(item);
			return item;
		}
	}

}
