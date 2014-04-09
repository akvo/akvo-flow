/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.surveyal.app.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;

import javax.servlet.http.HttpServletRequest;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.gis.geography.dao.CountryDao;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.gallatinsystems.metric.dao.MetricDao;
import com.gallatinsystems.metric.dao.SurveyMetricMappingDao;
import com.gallatinsystems.metric.domain.Metric;
import com.gallatinsystems.metric.domain.SurveyMetricMapping;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleClusterDao;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.gallatinsystems.surveyal.domain.SurveyedLocaleCluster;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * RESTFul servlet that can handle handle operations on SurveyedLocale and
 * related domain objects.
 * 
 * TODO: consider storing survey question list, metrics and mappings in a
 * Soft-Reference map to speed up processing.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyalRestServlet extends AbstractRestApiServlet {
	private static final long serialVersionUID = 5923399458369692813L;
	private static final String COMMUNITY_METRIC_NAME = "Community";
	private static final double TOLERANCE = 0.01;
	private static final double UNSET_VAL = -9999.9;
	private static final String DEFAULT = "DEFAULT";
	private static final String DEFAULT_ORG_PROP = "defaultOrg";
	private static final Logger log = Logger
			.getLogger(SurveyalRestServlet.class.getName());

	private SurveyInstanceDAO surveyInstanceDao;
	private SurveyedLocaleDao surveyedLocaleDao;
	private QuestionDao qDao;
	private CountryDao countryDao;
	private SurveyMetricMappingDao metricMappingDao;
	private MetricDao metricDao;
	private boolean useConfigStatusScore = false;
	private boolean useDynamicScoring = false;
	private String statusFragment;
	private Map<String, String> scoredVals;
	private boolean mergeNearby;

	/**
	 * initializes the servlet by instantiating all needed Dao classes and
	 * loading properties from the configuration.
	 * 
	 * 
	 */
	public SurveyalRestServlet() {
		surveyInstanceDao = new SurveyInstanceDAO();
		surveyedLocaleDao = new SurveyedLocaleDao();
		qDao = new QuestionDao();
		countryDao = new CountryDao();
		metricDao = new MetricDao();
		metricMappingDao = new SurveyMetricMappingDao();
		mergeNearby = false;
		String mergeProp = PropertyUtil.getProperty("mergeNearbyLocales");
		useDynamicScoring = Boolean.parseBoolean(PropertyUtil.getProperty("scoreLocaleDynmaic"));
		if (mergeProp != null && "false".equalsIgnoreCase(mergeProp.trim())) {
			mergeNearby = false;
		}
		// TODO: once the appropriate metric types are defined and reliably
		// assigned, consider removing this in favor of metrics
		statusFragment = PropertyUtil.getProperty("statusQuestionText");
		if (statusFragment != null && statusFragment.trim().length() > 0) {
			useConfigStatusScore = true;
			String[] fields = statusFragment.split(";");
			statusFragment = fields[0].toLowerCase();
			scoredVals = new HashMap<String, String>();
			if (fields.length > 1) {
				for (int i = 1; i < fields.length; i++) {
					if (fields[i].contains("=")) {
						String[] kvp = fields[i].split("=");
						scoredVals.put(kvp[0], kvp[1]);
					}
				}
			}
		}
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SurveyalRestRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		RestResponse resp = new RestResponse();
		SurveyalRestRequest sReq = (SurveyalRestRequest) req;
		if (SurveyalRestRequest.INGEST_INSTANCE_ACTION.equalsIgnoreCase(req
				.getAction())) {
			try {
				ingestSurveyInstance(sReq.getSurveyInstanceId());
			} catch (RuntimeException e) {
				log.log(Level.SEVERE,
						"Could not process instance: "
								+ sReq.getSurveyInstanceId() + ": "
								+ e.getMessage());
			}
		} else if (SurveyalRestRequest.RERUN_ACTION.equalsIgnoreCase(req
				.getAction())) {
			rerunForSurvey(sReq.getSurveyId());
		} else if (SurveyalRestRequest.REINGEST_INSTANCE_ACTION
				.equalsIgnoreCase(req.getAction())) {
			log.log(Level.INFO,
					"Reprocessing SurveyInstanceId: "
							+ sReq.getSurveyInstanceId());
			try {
				ingestSurveyInstance(sReq.getSurveyInstanceId());
			} catch (RuntimeException e) {
				log.log(Level.SEVERE,
						"Could not process instance: "
								+ sReq.getSurveyInstanceId() + ": "
								+ e.getMessage());
			}
		} else if (SurveyalRestRequest.POP_GEOCELLS_FOR_LOCALE_ACTION
				.equalsIgnoreCase(req.getAction())) {
				log.log(Level.INFO, "Creating geocells");
				populateGeocellsForLocale(req.getCursor());
		}
		return resp;
	}

	/**
	 * reruns the locale hydration for a survey
	 * 
	 * @param surveyId
	 */
	private void rerunForSurvey(Long surveyId) {
		if (surveyId != null) {
			Queue queue = QueueFactory.getDefaultQueue();
			Iterable<Entity> siList = surveyInstanceDao
					.listSurveyInstanceKeysBySurveyId(surveyId);
			if (siList != null) {
				int i = 0;
				for (Entity inst : siList) {
					if (inst != null && inst.getKey() != null) {
						String item = inst.getKey().toString();
						Integer startPos = item.indexOf("(");
						Integer endPos = item.indexOf(")");
						String surveyInstanceIdString = item.substring(
								startPos + 1, endPos);
						if (surveyInstanceIdString != null
								&& !surveyInstanceIdString.trim()
										.equalsIgnoreCase("")) {
							TaskOptions to = TaskOptions.Builder
									.withUrl("/app_worker/surveyalservlet")
									.param(SurveyalRestRequest.ACTION_PARAM,
											SurveyalRestRequest.REINGEST_INSTANCE_ACTION)
									.param(SurveyalRestRequest.SURVEY_INSTANCE_PARAM,
											surveyInstanceIdString);
							queue.add(to);

							i++;
						}
					} else {
						String instString = null;
						if (inst != null)
							instString = inst.toString();
						log.log(Level.INFO,
								"Inside rerunForSurvey in the null or empty instanceid branch: "
										+ instString);
					}
				}
				log.log(Level.INFO, "Submitted: " + i
						+ " SurveyInstances for remapping");
			}

		}
	}

	private void ingestSurveyInstance(Long surveyInstanceId) {
		SurveyInstance instance = surveyInstanceDao.getByKey(surveyInstanceId);
		if (instance != null) {
			ingestSurveyInstance(instance);
		} else
			log.log(Level.INFO,
					"Got to ingestSurveyInstance, but instance is null for surveyInstanceId: "
							+ surveyInstanceId);

	}

	/**
	 * looks up a surveyInstance by key and creates (or updates) a
	 * surveyedLocale based on the data contained therein. This method is
	 * unlikely to run in under 1 minute (based on datastore latency) so it is
	 * best invoked via a task queue
	 * 
	 * @param surveyInstanceId
	 */
	private void ingestSurveyInstance(SurveyInstance instance) {
		SurveyedLocale locale = null;
		if (instance != null) {
			List<QuestionAnswerStore> answers = surveyInstanceDao
					.listQuestionAnswerStore(instance.getKey().getId(), null);
			QuestionAnswerStore geoQ = null;
			SurveyDAO surveyDao = new SurveyDAO();
			Survey survey = surveyDao.getByKey(instance.getSurveyId());
			String pointType = null;
			if (survey != null) {
				pointType = survey.getPointType();
			}
			if (answers != null) {
				for (QuestionAnswerStore q : answers) {
					if (QuestionType.GEO.toString().equals(q.getType())) {
						geoQ = q;
						break;
					}
				}
			}

			if (instance.getSurveyedLocaleId() != null) {
				locale = surveyedLocaleDao.getByKey(instance
						.getSurveyedLocaleId());
			}
			GeoPlace geoPlace = null;

			// only create a "locale" if we have a valid geographic question
			if (geoQ != null && geoQ.getValue() != null && geoQ.getValue().length() > 0) {
				double lat = UNSET_VAL;
				double lon = UNSET_VAL;
				boolean ambiguousFlag = false;
				String code = null;
				String[] tokens = geoQ.getValue().split("\\|");
				if (tokens.length >= 2) {
					lat = Double.parseDouble(tokens[0]);
					lon = Double.parseDouble(tokens[1]);
					if (tokens.length >= 4) {
						code = tokens[tokens.length - 1];
					}
				}
				if (code == null) {
					Long codeNum = Long
							.parseLong((int) ((Math.abs(lat) * 10000d)) + ""
									+ (int) ((Math.abs(lon) * 10000d)));
					code = Long.toString(codeNum, 36);
				}
				if (lat == UNSET_VAL || lon == UNSET_VAL) {
					throw new RuntimeException(
							"Could not parse lat/lon from Geo Question "
									+ geoQ.getQuestionID());
				} else if (mergeNearby && locale == null) {
					// if we have a geo question but no locale id, see if we can
					// find one based on lat/lon
					List<SurveyedLocale> candidates = surveyedLocaleDao
							.listLocalesByCoordinates(pointType, lat, lon,
									TOLERANCE);
					if (candidates != null && candidates.size() == 1) {
						locale = candidates.get(0);
					} else if (candidates != null && candidates.size() > 1) {
						log.log(Level.WARNING,
								"Geo based lookup of surveyed locale returned more than one candidate so we are creating a new one");
						ambiguousFlag = true;
					}
				}
				geoPlace = getGeoPlace(lat, lon);
				if (locale == null) {
					locale = new SurveyedLocale();
					locale.setLastSurveyalInstanceId(instance.getKey().getId());
					locale.setLastSurveyedDate(instance.getCollectionDate());
					locale.setAmbiguous(ambiguousFlag);

					if (survey != null) {
						locale.setCreationSurveyId(survey.getKey().getId());
					}
					locale.setLatitude(lat);
					locale.setLongitude(lon);
					setGeoData(geoPlace, locale);

					// set Geocell data
					if (locale.getLatitude() != null
							&& locale.getLongitude() != null
							&& locale.getLongitude() < 180
							&& locale.getLatitude() < 180) {
						try {
							locale.setGeocells(GeocellManager
									.generateGeoCell(new Point(locale
											.getLatitude(), locale
											.getLongitude())));
						} catch (Exception ex) {
							log.log(Level.INFO,
									"Could not generate Geocell for locale: "
											+ locale.getKey().getId()
											+ " error: " + ex);
						}
					}

					if (survey != null) {
						locale.setLocaleType(survey.getPointType());
					}
					locale.setIdentifier(code);
					// TODO: for multi-org instances, set org on Survey and pull
					// from there
					if (locale.getOrganization() == null) {
						locale.setOrganization(PropertyUtil
								.getProperty(DEFAULT_ORG_PROP));
					}
					locale = surveyedLocaleDao.save(locale);

					// adjust Geocell cluster data
					// we first build a map of existing clusters
					// then either adapt an existing one, or create a new cluster
					// TODO when surveyedLocales are deleted, it needs to be substracted from the clusters.
					if (locale.getGeocells() != null){
						adaptClusterData(locale);
					} // end cluster data

				} else {
					if (survey.getPointType() != null
							&& !survey.getPointType().equals(
									locale.getLocaleType())) {
						locale.setLocaleType(survey.getPointType());
					}
				}
			}
			if (instance != null && geoPlace != null) {
				instance.setCountryCode(geoPlace.getCountryCode());
				instance.setSublevel1(geoPlace.getSub1());
				instance.setSublevel2(geoPlace.getSub2());
				instance.setSublevel3(geoPlace.getSub3());
				instance.setSublevel4(geoPlace.getSub4());
				instance.setSublevel5(geoPlace.getSub5());
				instance.setSublevel6(geoPlace.getSub6());
				if (answers != null) {
					// look for the Community metric
					List<Metric> metrics = metricDao.listMetrics(
							COMMUNITY_METRIC_NAME, null, null, null, null);
					if (metrics != null) {
						List<SurveyMetricMapping> mappings = new ArrayList<SurveyMetricMapping>();
						for (Metric m : metrics) {
							List<SurveyMetricMapping> temp = metricMappingDao
									.listMetricsBySurveyAndMetric(m.getKey()
											.getId(), instance.getSurveyId());
							if (temp != null) {
								mappings.addAll(temp);
							}
						}
						for (SurveyMetricMapping mapping : mappings) {
							for (QuestionAnswerStore ans : answers) {
								if (ans.getQuestionID().equals(
										mapping.getSurveyQuestionId().toString())) {
									instance.setCommunity(ans.getValue());
									break;
								}
							}
						}
					}
				}
			}

			if (locale != null && locale.getKey() != null && answers != null) {
				locale.setLastSurveyedDate(instance.getCollectionDate());
				locale.setLastSurveyalInstanceId(instance.getKey().getId());
				instance.setSurveyedLocaleId(locale.getKey().getId());
				List<SurveyalValue> values = constructValues(locale, answers);
				if (values != null) {
					surveyedLocaleDao.save(values);
					if (!useConfigStatusScore) {
						// now check the values to see if we have a status to
						// update
						// check the metrics first
						boolean found = false;
						for (SurveyalValue val : values) {
							if (isStatus(val.getMetricName())
									&& val.getStringValue() != null) {
								found = true;
								locale.setCurrentStatus(val.getStringValue());
								break;
							}
						}
						// if no luck, check the question text
						if (!found) {
							for (SurveyalValue val : values) {
								if (isStatus(val.getQuestionText())
										&& val.getStringValue() != null) {
									found = true;
									locale.setCurrentStatus(val
											.getStringValue());
									break;
								}
							}
						}
					} else if (useDynamicScoring) {
						
					} else {
						for (SurveyalValue val : values) {
							if (val.getQuestionText() != null
									&& val.getQuestionText().toLowerCase()
											.contains(statusFragment)) {
								String scoredField = scoredVals.get(val
										.getStringValue());
								if (scoredField == null) {
									scoredField = scoredVals.get(DEFAULT);
								}
								locale.setCurrentStatus(scoredField);
							}
						}
					}
				}
			}
		}
	}

	// this method is synchronised, because we are changing counts.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized void adaptClusterData(SurveyedLocale locale) {
		SurveyDAO sDao = new SurveyDAO();
		SurveyedLocaleClusterDao slcDao = new SurveyedLocaleClusterDao();
		SurveyInstanceDAO siDao = new SurveyInstanceDAO();
		Long surveyId = null;
		String surveyIdString = "";
		Boolean showOnPublicMap = false;
		SurveyInstance si = siDao.getByKey(locale.getLastSurveyalInstanceId());
		if (si != null) {
			surveyId = si.getSurveyId();
			surveyIdString = surveyId.toString();
		}

		// initialize the memcache
		Cache cache = null;
		Map props = new HashMap();
		props.put(GCacheFactory.EXPIRATION_DELTA, 12 * 60 * 60);
		props.put(MemcacheService.SetPolicy.SET_ALWAYS, true);
		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			cache = cacheFactory.createCache(props);
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Couldn't initialize cache: " + e.getMessage(), e);
		}

		if (cache == null) {
			return;
		}

		// get public status, first try from cache
		String pubKey = surveyIdString + "-publicStatus";
		if (cache.containsKey(pubKey)){
			showOnPublicMap = (Boolean) cache.get(pubKey);
		} else {
			Survey s = sDao.getByKey(surveyId);
			if (s != null){
				showOnPublicMap = showOnPublicMap || s.getPointType().equals("Point") || s.getPointType().equals("PublicInstitution");
				cache.put(pubKey, showOnPublicMap);
			}
		}

		Map<String, Long> cellMap;
		for (int i = 1 ; i <= 4 ; i++){
			String cell =  surveyIdString + "-" + locale.getGeocells().get(i);
			if (cache.containsKey(cell)){
				cellMap = (Map<String, Long>) cache.get(cell);
				addToCache(cache, cell,cellMap.get("id"), cellMap.get("count") + 1);
				SurveyedLocaleCluster clusterInStore = slcDao.getByKey(cellMap.get("id"));
				if (clusterInStore != null){
					clusterInStore.setCount(cellMap.get("count").intValue() + 1);
					slcDao.save(clusterInStore);
				}
				log.log(Level.INFO,"------------ got it from the cache");
			} else {
				// try to get it in the datastore. This can happen when the cache
				// has expired
				SurveyedLocaleCluster clusterInStore = slcDao.getExistingCluster(cell);
				if (clusterInStore != null){
					addToCache(cache, cell, clusterInStore.getKey().getId(),clusterInStore.getCount() + 1);
					clusterInStore.setCount(clusterInStore.getCount() + 1);
					slcDao.save(clusterInStore);
					log.log(Level.INFO,"------------ got it from the datastore");
				} else {
					// create a new one
					SurveyedLocaleCluster slcNew = new SurveyedLocaleCluster(locale.getLatitude(),
							locale.getLongitude(), locale.getGeocells().subList(0,i),
							locale.getGeocells().get(i), i + 1, locale.getKey().getId(), surveyId, showOnPublicMap,locale.getLastSurveyedDate());
					slcDao.save(slcNew);
					addToCache(cache, cell, slcNew.getKey().getId(),1);
					log.log(Level.INFO,"------------ made a new one");
				}
			}
		}
	}

	private void addToCache(Cache cache, String cell, Long id, long count){
		final Map<String, Long> v = new HashMap<String, Long>();
		v.put("count", count);
		v.put("id", id);
		cache.put(cell, v);
	}

	private boolean isStatus(String name) {
		if (name != null) {
			if (name.trim().toLowerCase().contains("status")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * tries several methods to resolve the lat/lon to a GeoPlace. If a geoPlace
	 * is found, looks for the country in the database and creates it if not
	 * found
	 * 
	 * @param lat
	 * @param lon
	 * @return
	 */
	private GeoPlace getGeoPlace(Double lat, Double lon) {
		GeoLocationServiceGeonamesImpl gs = new GeoLocationServiceGeonamesImpl();
		GeoPlace geoPlace = gs.manualLookup(lat.toString(), lon.toString(),
				OGRFeature.FeatureType.SUB_COUNTRY_OTHER);
		if (geoPlace == null) {
			geoPlace = gs.findGeoPlace(lat.toString(), lon.toString());
		}
		// check the country code to make sure it is in the database
		if (geoPlace != null && geoPlace.getCountryCode() != null) {
			Country country = countryDao.findByCode(geoPlace.getCountryCode());
			if (country == null) {
				country = new Country();
				country.setIsoAlpha2Code(geoPlace.getCountryCode());
				country.setName(geoPlace.getCountryName() != null ? geoPlace
						.getCountryName() : geoPlace.getCountryCode());
				country.setDisplayName(country.getName());
				countryDao.save(country);
			}
		}
		return geoPlace;
	}

	/**
	 * uses the geolocationService to determine the geographic sub-regions and
	 * country for a given point
	 * 
	 * @param l
	 */
	private void setGeoData(GeoPlace geoPlace, SurveyedLocale l) {
		if (geoPlace != null) {
			l.setCountryCode(geoPlace.getCountryCode());
			l.setSublevel1(geoPlace.getSub1());
			l.setSublevel2(geoPlace.getSub2());
			l.setSublevel3(geoPlace.getSub3());
			l.setSublevel4(geoPlace.getSub4());
			l.setSublevel5(geoPlace.getSub5());
			l.setSublevel6(geoPlace.getSub6());
		}
	}

	/**
	 * converts QuestionAnswerStore objects into SurveyalValues, copying the
	 * overlapping values from SurveyedLocale as needed. The surveydLocale must
	 * have been saved prior to calling this method if one expects the
	 * surveyedLocaleId member to be populated.
	 * 
	 * @param l
	 * @param answers
	 * @return
	 */
	private List<SurveyalValue> constructValues(SurveyedLocale l,
			List<QuestionAnswerStore> answers) {
		List<SurveyalValue> values = new ArrayList<SurveyalValue>();
		if (answers != null && answers.size() > 0) {
			List<SurveyMetricMapping> mappings = null;
			List<SurveyalValue> oldVals = surveyedLocaleDao
					.listSurveyalValuesByInstance(answers.get(0)
							.getSurveyInstanceId());
			List<Metric> metrics = null;
			boolean loadedItems = false;
			List<Question> questionList = qDao.listQuestionsBySurvey(answers.get(0).getSurveyId());

			// date value
			Calendar cal = new GregorianCalendar();
			for (QuestionAnswerStore ans : answers) {
				if (!loadedItems && ans.getSurveyId() != null) {
					metrics = metricDao.listMetrics(null, null, null,
							l.getOrganization(), "all");
					mappings = metricMappingDao.listMappingsBySurvey(ans
							.getSurveyId());
					loadedItems = true;
				}
				SurveyalValue val = null;
				if (oldVals != null) {
					for (SurveyalValue oldVal : oldVals) {
						if (oldVal.getSurveyQuestionId() != null
								&& oldVal.getSurveyQuestionId().toString()
										.equals(ans.getQuestionID())) {
							val = oldVal;
						}
					}
				}
				if (val == null) {
					val = new SurveyalValue();
				}
				val.setSurveyedLocaleId(l.getKey().getId());
				val.setCollectionDate(ans.getCollectionDate());
				val.setCountryCode(l.getCountryCode());

				if (ans.getCollectionDate() != null) {
					cal.setTime(ans.getCollectionDate());
				}
				val.setDay(cal.get(Calendar.DAY_OF_MONTH));
				val.setMonth(cal.get(Calendar.MONTH) + 1);
				val.setYear(cal.get(Calendar.YEAR));
				val.setLocaleType(l.getLocaleType());
				val.setStringValue(ans.getValue());
				val.setValueType(SurveyalValue.STRING_VAL_TYPE);
				if (ans.getValue() != null) {
					try {

						Double d = Double.parseDouble(ans.getValue().trim());
						val.setNumericValue(d);
						val.setValueType(SurveyalValue.NUM_VAL_TYPE);
					} catch (Exception e) {
						// no-op
					}
				}
				if (metrics != null && mappings != null) {
					metriccheck: for (SurveyMetricMapping mapping : mappings) {
						if (ans.getQuestionID() != null
								&& Long.parseLong(ans.getQuestionID()) == mapping
										.getSurveyQuestionId()) {
							for (Metric m : metrics) {
								if (mapping.getMetricId() == m.getKey().getId()) {
									val.setMetricId(m.getKey().getId());
									val.setMetricName(m.getName());
									val.setMetricGroup(m.getGroup());
									break metriccheck;
								}
							}
						}
					}
				}
				// TODO: resolve score
				val.setOrganization(l.getOrganization());
				val.setSublevel1(l.getSublevel1());
				val.setSublevel2(l.getSublevel2());
				val.setSublevel3(l.getSublevel3());
				val.setSublevel4(l.getSublevel4());
				val.setSublevel5(l.getSublevel5());
				val.setSublevel6(l.getSublevel6());
				val.setSurveyInstanceId(ans.getSurveyInstanceId());
				val.setSystemIdentifier(l.getSystemIdentifier());
				if (questionList != null) {
					for (Question q : questionList) {
						if (ans.getQuestionID() != null
								&& Long.parseLong(ans.getQuestionID()) == q
										.getKey().getId()) {
							val.setQuestionText(q.getText());
							val.setSurveyQuestionId(q.getKey().getId());
							val.setQuestionType(q.getType().toString());
							break;
						}
					}
				}
				values.add(val);
			}
		}
		return values;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
	}

	/**
	* runs over all surveydLocale objects, and populates:
	* the Geocells field based on the latitude and longitude.
	*
	* New surveyedLocales will have these fields populated automatically, this
	* method is to update legacy data.
	*
	* This method is invoked as a URL request:
	* http://..../rest/actions?action=populateGeocellsForLocale
	* @param cursor
	* */
	private void populateGeocellsForLocale(String cursor) {
	log.log(Level.INFO, "creating geocells, at least, trying " + cursor);
		List<SurveyedLocale> slList = null;
		SurveyedLocaleDao slDao = new SurveyedLocaleDao();
		slList = slDao.list(cursor);
		String newCursor = SurveyedLocaleDao.getCursor(slList);
		Integer num = slList.size();

		if (slList != null && slList.size() > 0) {
			for (SurveyedLocale sl : slList) {
				// populate geocells
				if (sl.getGeocells() == null || sl.getGeocells().size() == 0) {
					if (sl.getLatitude() != null && sl.getLongitude() != null
						&& sl.getLongitude() < 180
						&& sl.getLatitude() < 180) {
						try {
							sl.setGeocells(GeocellManager.generateGeoCell(new Point(
									sl.getLatitude(), sl.getLongitude())));
							} catch (Exception ex) {
								log.log(Level.INFO,"Could not generate Geocell for AP: "
									+ sl.getKey().getId() + " error: " + ex);
							}
						}
					}
					slDao.save(sl);
			}
		}
		if (num > 0) {
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder
				.withUrl("/app_worker/surveyalservlet")
				.param(SurveyalRestRequest.ACTION_PARAM,
					SurveyalRestRequest.POP_GEOCELLS_FOR_LOCALE_ACTION)
				.param("cursor", newCursor));
		}
	}
}
