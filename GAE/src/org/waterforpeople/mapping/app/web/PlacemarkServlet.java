package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDto;
import org.waterforpeople.mapping.app.web.dto.PlacemarkRestRequest;
import org.waterforpeople.mapping.app.web.dto.PlacemarkRestResponse;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class PlacemarkServlet extends AbstractRestApiServlet {
	private static final long serialVersionUID = -9031594440737716966L;
	private static final Logger log = Logger.getLogger(PlacemarkServlet.class
			.getName());

	private KMLGenerator kmlGen = new KMLGenerator();
	private Cache cache;
	private AccessPointDao apDao;

	@SuppressWarnings("unchecked")
	public PlacemarkServlet() {
		super();
		apDao = new AccessPointDao();
		CacheFactory cacheFactory;
		try {
			cacheFactory = CacheManager.getInstance().getCacheFactory();
			Map configMap = new HashMap();
			configMap.put(GCacheFactory.EXPIRATION_DELTA, 3600);
			configMap.put(MemcacheService.SetPolicy.SET_ALWAYS, true);
			cache = cacheFactory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
			log.log(Level.SEVERE, "Could not initialize cache", e);

		}
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new PlacemarkRestRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		PlacemarkRestRequest piReq = (PlacemarkRestRequest) req;
		if (cache != null && !piReq.getIgnoreCache()) {
			PlacemarkRestResponse cachedResponse = null;
			try {
				cachedResponse = (PlacemarkRestResponse) cache.get(piReq
						.getCacheKey());
			} catch (Throwable t) {
				log.log(Level.WARNING, "Could not look up data in cache", t);
			}
			if (cachedResponse != null) {
				return cachedResponse;
			}
		}
		PlacemarkRestResponse response = null;
		// if we had a cache miss (or the cache is not available), then hit the
		// datastore and cachethe resupt
		if (piReq.getAction() != null
				&& PlacemarkRestRequest.GET_AP_DETAILS_ACTION.equals(piReq
						.getAction())) {

			AccessPoint ap = (AccessPoint) apDao.findAccessPoint(
					piReq.getCommunityCode(), piReq.getPointType());
			List<AccessPoint> apList = new ArrayList<AccessPoint>();
			apList.add(ap);
			response = (PlacemarkRestResponse) convertToResponse(apList, true,
					null, null, piReq.getDisplay());

		} else {
			// ListPlacemarks Action
			if (piReq.getLat1() != null) {
				List<AccessPoint> results = apDao
						.listAccessPointsByBoundingBox(piReq.getPointType(),
								piReq.getLat1(), piReq.getLat2(),
								piReq.getLong1(), piReq.getLong2(),
								piReq.getCursor());
				response = (PlacemarkRestResponse) convertToResponse(results,
						true, AccessPointDao.getCursor(results),
						piReq.getCursor(), piReq.getDisplay());
			} else if (piReq.getSubLevel() != null) {
				List<AccessPoint> results = apDao.listBySubLevel(
						piReq.getCountry(), piReq.getSubLevel(),
						piReq.getSubLevelValue(), piReq.getCursor(),
						AccessPointType.WATER_POINT);
				String display = null;
				if (piReq.getDisplay() != null) {
					display = piReq.getDisplay();
				}
				response = (PlacemarkRestResponse) convertToResponse(results,
						piReq.getNeedDetailsFlag(),
						AccessPointDao.getCursor(results), piReq.getCursor(),
						display);
			} else {
				int desiredResults = 20;
				if (piReq.getDesiredResults() > 20) {
					if (piReq.getDesiredResults() > 500) {
						desiredResults = 500;
					} else {
						desiredResults = piReq.getDesiredResults();
					}
				}

				List<AccessPoint> results = apDao.searchAccessPoints(
						piReq.getCountry(), null, null, null, null, null, null,
						null, null, null, desiredResults, piReq.getCursor());
				String display = null;
				if (piReq.getDisplay() != null) {
					display = piReq.getDisplay();
				}
				response = (PlacemarkRestResponse) convertToResponse(results,
						piReq.getNeedDetailsFlag(),
						AccessPointDao.getCursor(results), piReq.getCursor(),
						display);
			}
		}
		if (response != null && cache != null  && piReq.getLat1()==null) {
			try {
				cache.put(piReq.getCacheKey(), response);
			} catch (Throwable t) {
				log.log(Level.WARNING, "Could not cache results", t);
			}
		}
		return response;
	}

	private RestResponse convertToResponse(List<AccessPoint> apList,
			Boolean needDetailsFlag, String cursor, String oldCursor,
			String display) {
		PlacemarkRestResponse resp = new PlacemarkRestResponse();
		if (needDetailsFlag == null)
			needDetailsFlag = true;
		if (apList != null) {
			List<PlacemarkDto> dtoList = new ArrayList<PlacemarkDto>();

			for (AccessPoint ap : apList) {
				if (!ap.getPointType().equals(AccessPointType.SANITATION_POINT)) {
					dtoList.add(marshallDomainToDto(ap, needDetailsFlag,
							display));
				}
				resp.setPlacemarks(dtoList);
			}
		}
		if (cursor != null) {
			if (oldCursor == null || !cursor.equals(oldCursor)) {
				resp.setCursor(cursor);
			}
		} else {
			resp.setCursor(null);
		}
		return resp;
	}

	private PlacemarkDto marshallDomainToDto(AccessPoint ap,
			Boolean needDetailsFlag, String display) {
		PlacemarkDto pdto = new PlacemarkDto();
		pdto.setPinStyle(KMLGenerator.encodePinStyle(ap.getPointType(),
				ap.getPointStatus()));
		pdto.setLatitude(ap.getLatitude());
		pdto.setLongitude(ap.getLongitude());
		pdto.setIconUrl(getUrlFromStatus(ap.getPointStatus(), ap.getPointType()));
		pdto.setCommunityCode(ap.getCommunityCode());
		pdto.setMarkType(ap.getPointType().toString());
		pdto.setCollectionDate(ap.getCollectionDate());
		if (needDetailsFlag) {
			String placemarkString = null;
			try {
				placemarkString = kmlGen.bindPlacemark(ap,
						"placemarkExternalMap.vm", display);
				pdto.setPlacemarkContents(placemarkString);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Could not bind placemarks", e);
			}
		}
		return pdto;
	}

	private String getUrlFromStatus(Status status,
			AccessPoint.AccessPointType pointType) {
		if (status == null) {
			return "Unknown";
		}
		if (AccessPointType.WATER_POINT.equals(pointType)) {
			if (status.equals(AccessPoint.Status.FUNCTIONING_HIGH)) {
				return KMLGenerator.WATER_POINT_FUNCTIONING_GREEN_ICON_URL;
			} else if (status.equals(AccessPoint.Status.FUNCTIONING_OK)
					|| status
							.equals(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
				return KMLGenerator.WATER_POINT_FUNCTIONING_YELLOW_ICON_URL;
			} else if (status.equals(AccessPoint.Status.BROKEN_DOWN)) {
				return KMLGenerator.WATER_POINT_FUNCTIONING_RED_ICON_URL;
			} else if (status.equals(AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
				return KMLGenerator.WATER_POINT_FUNCTIONING_BLACK_ICON_URL;
			} else {
				return KMLGenerator.WATER_POINT_FUNCTIONING_BLACK_ICON_URL;
			}
		} else if (AccessPointType.PUBLIC_INSTITUTION.equals(pointType)) {
			if (status.equals(AccessPoint.Status.FUNCTIONING_HIGH)) {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_GREEN_ICON_URL;
			} else if (status.equals(AccessPoint.Status.FUNCTIONING_OK)
					|| status
							.equals(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL;
			} else if (status.equals(AccessPoint.Status.BROKEN_DOWN)) {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_RED_ICON_URL;
			} else if (status.equals(AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_BLACK_ICON_URL;
			} else {
				return KMLGenerator.PUBLIC_INSTITUTION_FUNCTIONING_BLACK_ICON_URL;
			}
		} else if (AccessPointType.SCHOOL.equals(pointType)) {
			if (status.equals(AccessPoint.Status.FUNCTIONING_HIGH)) {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_GREEN_ICON_URL;
			} else if (status.equals(AccessPoint.Status.FUNCTIONING_OK)
					|| status
							.equals(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL;
			} else if (status.equals(AccessPoint.Status.BROKEN_DOWN)) {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_RED_ICON_URL;
			} else if (status.equals(AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_BLACK_ICON_URL;
			} else {
				return KMLGenerator.SCHOOL_INSTITUTION_FUNCTIONING_BLACK_ICON_URL;
			}
		}
		return null;

	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		PlacemarkRestResponse piResp = (PlacemarkRestResponse) resp;
		JSONObject result = new JSONObject(piResp);
		getResponse().getWriter().println(result.toString());
	}
}
