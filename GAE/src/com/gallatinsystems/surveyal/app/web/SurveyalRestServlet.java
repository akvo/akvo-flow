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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;

import javax.servlet.http.HttpServletRequest;

import net.sf.jsr107cache.Cache;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.common.util.MemCacheUtils;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.gis.geography.dao.CountryDao;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;
import com.gallatinsystems.gis.map.MapUtils;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.gallatinsystems.metric.dao.MetricDao;
import com.gallatinsystems.metric.dao.SurveyMetricMappingDao;
import com.gallatinsystems.metric.domain.Metric;
import com.gallatinsystems.metric.domain.SurveyMetricMapping;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyalValueDao;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.Entity;
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
		} else if (SurveyalRestRequest.ADAPT_CLUSTER_DATA_ACTION
				.equalsIgnoreCase(req.getAction())) {
			log.log(Level.INFO, "adapting cluster data");
			adaptClusterData(sReq.getSurveyedLocaleId());
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
			Long surveyGroupId = null;
			if (survey != null) {
				pointType = survey.getPointType();
				surveyGroupId = survey.getSurveyGroupId();
			}

			// if the surveyed locale id was available in the ingested data,
			// this has been set in the save method in surveyInstanceDao.
			boolean useExistingLocale = false;
			if (instance.getSurveyedLocaleId() != null) {
				locale = surveyedLocaleDao.getByKey(instance
						.getSurveyedLocaleId());
				if (locale != null) {
					useExistingLocale = true;
				}
			}

			// try to construct geoPlace. Geo information can come from two sources:
			// 1) the META_GEO information in the surveyInstance, and
			// 2) a geo question. 
			// If we can't find geo information in 1), we try 2)

			GeoPlace geoPlace = null;
			String geoString = null;
			double lat = UNSET_VAL;
			double lon = UNSET_VAL;

			// if the GEO information was present as Meta data, get it from there
			if (instance.getLocaleGeoLocation() != null && instance.getLocaleGeoLocation().length() > 0) {
				geoString = instance.getLocaleGeoLocation();
			// else, try to look for a GEO question
			} else {
				if (answers != null) {
					for (QuestionAnswerStore q : answers) {
						if (QuestionType.GEO.toString().equals(q.getType())) {
							geoQ = q;
							break;
						}
					}
					if (geoQ != null && geoQ.getValue() != null
							&& geoQ.getValue().length() > 0) {
						geoString = geoQ.getValue();
					}
				}
			}

			if (geoString != null && geoString.length() > 0) {
				String[] tokens = geoString.split("\\|");
				if (tokens.length >= 2) {
					try {
						lat = Double.parseDouble(tokens[0]);
						lon = Double.parseDouble(tokens[1]);
					} catch (NumberFormatException nfe) {
						log.log(Level.SEVERE,
								"Could not parse lat/lon from Geo Question "
										+ geoQ.getQuestionID());
					}
				}
				if (lat != UNSET_VAL && lon != UNSET_VAL) {
					geoPlace = getGeoPlace(lat, lon);
				}
			}

			// if we have a geoPlace, set it on the instance
			if (instance != null && geoPlace != null) {
				instance.setCountryCode(geoPlace.getCountryCode());
				instance.setSublevel1(geoPlace.getSub1());
				instance.setSublevel2(geoPlace.getSub2());
				instance.setSublevel3(geoPlace.getSub3());
				instance.setSublevel4(geoPlace.getSub4());
				instance.setSublevel5(geoPlace.getSub5());
				instance.setSublevel6(geoPlace.getSub6());
			}


			// if we don't have a locale, create one
			if (locale == null) {
				locale = new SurveyedLocale();
				if (lat != UNSET_VAL && lon != UNSET_VAL) {
					locale.setLatitude(lat);
					locale.setLongitude(lon);
				}
				locale.setSurveyGroupId(surveyGroupId);
				if (instance.getSurveyedLocaleIdentifier() != null && instance.getSurveyedLocaleIdentifier().trim().length() > 0){
					locale.setIdentifier(instance.getSurveyedLocaleIdentifier());
				} else {
					// if we don't have an identifier, create a random UUID.

					locale.setIdentifier(base32Uuid());
				}
				if (survey != null) {
					locale.setLocaleType(pointType);
				}

				if (locale.getOrganization() == null) {
					locale.setOrganization(PropertyUtil
							.getProperty(DEFAULT_ORG_PROP));
				}
			}
			
			if (locale != null){
				locale.setLastSurveyedDate(instance.getCollectionDate());
				locale.setLastSurveyalInstanceId(instance.getKey().getId());

				// add surveyInstanceId to list of contributed surveyInstances
				List<Long> surveyInstanceContrib = locale.getSurveyInstanceContrib();
				if (surveyInstanceContrib == null) {
					List<Long> newList = new ArrayList<Long>();
					newList.add(instance.getKey().getId());
					locale.setSurveyInstanceContrib(newList);
				} else {
					if (!surveyInstanceContrib.contains(instance.getKey().getId())) {
						surveyInstanceContrib.add(instance.getKey().getId());
						locale.setSurveyInstanceContrib(surveyInstanceContrib);
						}
					}

				if (instance.getSurveyedLocaleDisplayName() != null && 
						instance.getSurveyedLocaleDisplayName().length() > 0){
					locale.setDisplayName(instance.getSurveyedLocaleDisplayName());
				}

				// if we have geoinformation, we will use it on the locale provided that:
				// 1) it is a new Locale, or 2) it was brought in as meta information, meaning it should 
				// overwrite previous locale geo information
				if (instance.getLocaleGeoLocation() != null || !useExistingLocale){
					if (geoPlace != null) {
						setGeoData(geoPlace, locale);
					}

					if (lat != UNSET_VAL && lon != UNSET_VAL) {
						locale.setLatitude(lat);
						locale.setLongitude(lon);
					}
				}

				if (survey.getPointType() != null && !survey.getPointType()
								.equals(locale.getLocaleType())) {
					locale.setLocaleType(survey.getPointType());
				}

				locale = surveyedLocaleDao.save(locale);
			}

			// save the surveyalValues
			if (locale != null && locale.getKey() != null && answers != null) {
				locale.setLastSurveyedDate(instance.getCollectionDate());
				locale.setLastSurveyalInstanceId(instance.getKey().getId());
				instance.setSurveyedLocaleId(locale.getKey().getId());
				List<SurveyalValue> values = constructValues(locale, answers);
				if (values != null) {
					surveyedLocaleDao.save(values);
				}
				surveyedLocaleDao.save(locale);
				surveyInstanceDao.save(instance);
			}
		}
	}

	/* Creates a base32 version of a UUID. in the output, it replaces the following letters:
     * l, o, i are replace by w, x, y, to avoid confusion with 1 and 0
     * we don't use the z as it can easily be confused with 2, especially in handwriting.
     * If we can't form the base32 version, we return an empty string.
     * The same code is used in the FLOW Mobile app: https://github.com/akvo/akvo-flow-mobile/blob/feature/pointupdates/survey/
     * src/com/gallatinsystems/survey/device/util/Base32.java
     */
    public static String base32Uuid(){
        final String uuid = UUID.randomUUID().toString();
        String strippedUUID = (uuid.substring(0,13) + uuid.substring(24,27)).replace("-", "");
        String result = null;
        try {
            Long id = Long.parseLong(strippedUUID,16);
            result = Long.toString(id,32).replace("l","w").replace("o","x").replace("i","y");
        } catch (NumberFormatException e){
            // if we can't create the base32 UUID string, return the original uuid.
            result = uuid;
        }

        return result;
    }

	// this method is synchronised, because we are changing counts.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized void adaptClusterData(Long surveyedLocaleId) {
		final SurveyedLocaleDao slDao = new SurveyedLocaleDao();
		final SurveyedLocale locale = slDao.getById(surveyedLocaleId);
		
		if (locale == null){
			log.log(Level.SEVERE,
					"Couldn't find surveyedLocale with id: " + surveyedLocaleId);
			return;
		}

		// initialize cache
		Cache cache = MemCacheUtils.initCache(12 * 60 * 60); // 12 hours

		if (cache == null) {
			// reschedule task to run in 5 mins
			Queue queue = QueueFactory.getDefaultQueue();
			TaskOptions to = TaskOptions.Builder
					.withUrl("/app_worker/surveyalservlet")
					.param(SurveyalRestRequest.ACTION_PARAM,
							SurveyalRestRequest.ADAPT_CLUSTER_DATA_ACTION)
					.param(SurveyalRestRequest.SURVEYED_LOCALE_PARAM,
							surveyedLocaleId + "")
					.countdownMillis(5 * 1000 * 60); // 5 minutes
			queue.add(to);
			return;
		}
		
		MapUtils.recomputeCluster(cache, locale);

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<SurveyalValue> constructValues(SurveyedLocale l,
			List<QuestionAnswerStore> answers) {
		List<SurveyalValue> values = new ArrayList<SurveyalValue>();
		if (answers != null && answers.size() > 0) {
			String key = null;
			Integer questionGroupOrder = null;
			Question q = null;
			Long questionId = null;
			QuestionGroupDao qgDao = new QuestionGroupDao();

			Cache cache = MemCacheUtils.initCache(12 * 60 * 60); // 12 hours

			List<SurveyMetricMapping> mappings = null;
			List<SurveyalValue> oldVals = surveyedLocaleDao
					.listSurveyalValuesByInstance(answers.get(0)
							.getSurveyInstanceId());
			List<Metric> metrics = null;
			boolean loadedItems = false;
			List<Question> questionList = qDao.listQuestionsBySurvey(answers.get(0).getSurveyId());

			// put questions in map for easy retrieval
			Map qMap = new HashMap<Long,Integer>();
			Integer index = 0;
			if (questionList != null){
				for (Question qu : questionList){
					qMap.put(qu.getKey().getId(), index);
					index++;
				}
			}

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
				val.setSurveyId(ans.getSurveyId());
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

				questionId = null;
				if (ans.getQuestionID() != null){
					try {
						questionId = Long.parseLong(ans.getQuestionID());
					} catch (NumberFormatException e){
						log.log(Level.SEVERE,
								"Could not create surveyal value for question answer: "
										+ ans.getKey().getId() + ": "
												+ "can't parse questionId.");
					}
				}

				if (questionId != null &&
						qMap.containsKey(questionId)){
					q = questionList.get((Integer) qMap.get(questionId));
					val.setQuestionText(q.getText());
					val.setSurveyQuestionId(q.getKey().getId());
					val.setQuestionType(q.getType().toString());
					val.setQuestionOrder(q.getOrder());
					val.setSurveyId(q.getSurveyId());

					// try to get question group order from cache
					key = "qg-order-" + q.getQuestionGroupId();
					if (cache != null && cache.containsKey(key)){
						 questionGroupOrder = (Integer) cache.get(key);
					} else {
						// if not in cache, find it in datastore
						QuestionGroup qg = qgDao.getByKey(q.getQuestionGroupId());
						if (qg != null){
							questionGroupOrder = qg.getOrder();
							if (cache != null){
								MemCacheUtils.putObject(cache, key, questionGroupOrder);
							}
						}
					}
					val.setQuestionGroupOrder(questionGroupOrder);
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
