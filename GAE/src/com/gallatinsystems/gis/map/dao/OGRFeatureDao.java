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

	public List<OGRFeature> listBySubLevelCountryName(String countryCode,
			Integer level, String subLevelValue, String cursorString,
			String parentSubPath) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(OGRFeature.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("countryCode", filterString, paramString, "String",
				countryCode, paramMap, EQ_OP);
		if (level > 0)
			appendNonNullParam("sub" + level, filterString, paramString,
					"String", subLevelValue, paramMap, EQ_OP);

		if (parentSubPath != null) {
			String[] subParts = parentSubPath.split("/");

			for (int i = 1; i < subParts.length; i++) {
				// first param is country so ignore
				appendNonNullParam("sub" + i, filterString, paramString,
						"String", subParts[i], paramMap, EQ_OP);
			}
		}
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		prepareCursor(cursorString, query);
		@SuppressWarnings("unchecked")
		List<OGRFeature> resultsGTE = (List<OGRFeature>) query
				.executeWithMap(paramMap);

		return resultsGTE;
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
			if (y1 < boundingBox[3]) {
				results.add(item);
			}
		}

		return results;
	}

	public List<OGRFeature> listBySubLevelCountry(String countryCode,
			Integer subLevel, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(OGRFeature.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();
		appendNonNullParam("countryCode", filterString, paramString, "String",
				countryCode, paramMap, EQ_OP);
		appendNonNullParam("featureType", filterString, paramString, "String",
				FeatureType.SUB_COUNTRY_OTHER, paramMap, EQ_OP);
		appendNonNullParam("sub" + (subLevel), filterString, paramString,
				"String", "null", paramMap, NOT_EQ_OP);
		appendNonNullParam("sub" + (subLevel + 1), filterString, paramString,
				"String", "null", paramMap, EQ_OP);
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		prepareCursor(cursorString, query);
		@SuppressWarnings("unchecked")
		List<OGRFeature> resultsGTE = (List<OGRFeature>) query
				.executeWithMap(paramMap);
		List<OGRFeature> results = new ArrayList<OGRFeature>();

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

	public OGRFeature findByCountryTypeAndSub(String countryCode, String name,
			FeatureType featureType, ArrayList<String> subArray) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(OGRFeature.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("featureType", filterString, paramString, "String",
				featureType, paramMap, EQ_OP);
		appendNonNullParam("name", filterString, paramString, "String", name,
				paramMap, EQ_OP);
		appendNonNullParam("countryCode", filterString, paramString, "String",
				countryCode, paramMap, EQ_OP);
		for (int i = 1; i < subArray.size() + 1; i++) {
			appendNonNullParam("sub" + i, filterString, paramString, "String",
					subArray.get(i - 1), paramMap, EQ_OP);
		}

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
			ArrayList<String> subList = new ArrayList<String>();
			if (item.getSub1() != null) {
				subList.add(item.getSub1());
			}
			if (item.getSub2() != null) {
				subList.add(item.getSub2());
			}
			if (item.getSub3() != null) {
				subList.add(item.getSub3());
			}
			if (item.getSub4() != null) {
				subList.add(item.getSub4());
			}
			if (item.getSub5() != null) {
				subList.add(item.getSub5());
			}
			if (item.getSub6() != null) {
				subList.add(item.getSub6());
			}

			OGRFeature existingItem = findByCountryTypeAndSub(
					item.getCountryCode(), item.getName(),
					FeatureType.SUB_COUNTRY_OTHER, subList);
			if (existingItem != null) {
				boolean isSame = true;
				if (item.getSub1() != null && existingItem.getSub1() != null
						&& !existingItem.getSub1().equals(item.getSub1())) {
					isSame = false;
				}
				if ((item.getSub2() != null && existingItem.getSub2() != null && !existingItem
						.getSub2().equals(item.getSub2()))
						|| (item.getSub2() == null && existingItem.getSub2() != null)) {
					isSame = false;
				}
				if (item.getSub3() != null && existingItem.getSub3() != null
						&& !existingItem.getSub3().equals(item.getSub3())) {
					isSame = false;
				}
				if (item.getSub4() != null && existingItem.getSub4() != null
						&& !existingItem.getSub4().equals(item.getSub4())) {
					isSame = false;
				}
				if (item.getSub5() != null && existingItem.getSub5() != null
						&& !existingItem.getSub5().equals(item.getSub5())) {
					isSame = false;
				}
				if (item.getSub6() != null && existingItem.getSub6() != null
						&& !existingItem.getSub6().equals(item.getSub6())) {
					isSame = false;
				}
				if (isSame) {
					existingItem.setGeometry(item.getGeometry());
					existingItem.setBoundingBox(item.getBoundingBox());
					super.save(existingItem);
					return existingItem;
				}
			}
			super.save(item);
			return item;
		}
	}

}
