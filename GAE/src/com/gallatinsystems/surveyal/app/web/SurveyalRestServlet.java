package com.gallatinsystems.surveyal.app.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

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
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

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
	private static final double TOLERANCE = 0.01;
	private static final double UNSET_VAL = -9999.9;
	private static final String DEFAULT_ORG_PROP = "defaultOrg";
	private static final Logger log = Logger
			.getLogger(SurveyalRestServlet.class.getName());

	private SurveyInstanceDAO surveyInstanceDao;
	private SurveyedLocaleDao surveyedLocaleDao;
	private QuestionDao qDao;
	private CountryDao countryDao;
	private SurveyMetricMappingDao metricMappingDao;
	private MetricDao metricDao;

	public SurveyalRestServlet() {
		surveyInstanceDao = new SurveyInstanceDAO();
		surveyedLocaleDao = new SurveyedLocaleDao();
		qDao = new QuestionDao();
		countryDao = new CountryDao();
		metricDao = new MetricDao();
		metricMappingDao = new SurveyMetricMappingDao();

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
		}
		return resp;
	}

	/**
	 * looks up a surveyInstance by key and creates (or updates) a
	 * surveyedLocale based on the data contained therein. This method is
	 * unlikely to run in under 1 minute (based on datastore latency) so it is
	 * best invoked via a task queue
	 * 
	 * @param surveyInstanceId
	 */
	private void ingestSurveyInstance(Long surveyInstanceId) {
		SurveyInstance instance = surveyInstanceDao.getByKey(surveyInstanceId);
		SurveyedLocale locale = null;
		if (instance != null) {
			List<QuestionAnswerStore> answers = surveyInstanceDao
					.listQuestionAnswerStore(surveyInstanceId, null);
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

			// only create a "locale" if we have a geographic question
			if (locale == null && geoQ != null && geoQ.getValue() != null) {
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
				} else {
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
				if (locale == null) {
					locale = new SurveyedLocale();
					locale.setAmbiguous(ambiguousFlag);
					locale.setLatitude(lat);
					locale.setLongitude(lon);
					setGeoData(locale);
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
				}
			}
			if (locale != null && locale.getKey() != null && answers != null) {
				locale.setLastSurveyedDate(instance.getCollectionDate());
				locale.setLastSurveyalInstanceId(instance.getKey().getId());
				instance.setSurveyedLocaleId(locale.getKey().getId());
				List<SurveyalValue> values = constructValues(locale, answers);
				if (values != null) {
					surveyedLocaleDao.save(values);
					// now check the values to see if we have a status to update
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
								locale.setCurrentStatus(val.getStringValue());
								break;
							}
						}
					}
				}
			}
		}
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
	 * uses the geolocationService to determine the geographic sub-regions and
	 * country for a given point
	 * 
	 * @param l
	 */
	private void setGeoData(SurveyedLocale l) {

		GeoLocationServiceGeonamesImpl gs = new GeoLocationServiceGeonamesImpl();
		GeoPlace geoPlace = gs.manualLookup(l.getLatitude().toString(), l
				.getLongitude().toString(),
				OGRFeature.FeatureType.SUB_COUNTRY_OTHER);
		String countryCode = null;
		String countryName = null;
		if (geoPlace != null) {
			countryCode = geoPlace.getCountryCode();
			countryName = geoPlace.getCountryName();
			l.setCountryCode(geoPlace.getCountryCode());
			l.setSublevel1(geoPlace.getSub1());
			l.setSublevel2(geoPlace.getSub2());
			l.setSublevel3(geoPlace.getSub3());
			l.setSublevel4(geoPlace.getSub4());
			l.setSublevel5(geoPlace.getSub5());
			l.setSublevel6(geoPlace.getSub6());
		} else if (geoPlace == null || l.getCountryCode() == null) {
			GeoPlace geoPlaceCountry = gs.findGeoPlace(l.getLatitude()
					.toString(), l.getLongitude().toString());
			if (geoPlaceCountry != null) {
				l.setCountryCode(geoPlaceCountry.getCountryCode());
				countryCode = geoPlaceCountry.getCountryCode();
				countryName = geoPlaceCountry.getCountryName();
			}
		}
		// check the country code to make sure it is in the database
		if (countryCode != null) {
			Country country = countryDao.findByCode(countryCode);
			if (country == null) {
				country = new Country();
				country.setIsoAlpha2Code(countryCode);
				country.setName(countryName != null ? countryName : countryCode);
				country.setDisplayName(country.getName());
				countryDao.save(country);
			}
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
		if (answers != null) {
			List<SurveyMetricMapping> mappings = null;
			List<Metric> metrics = null;
			boolean loadedItems = false;
			List<Question> questionList = null;
			// initialize outside the loop so all answers get same collection
			// date value
			Calendar cal = new GregorianCalendar();
			for (QuestionAnswerStore ans : answers) {
				if (!loadedItems && ans.getSurveyId() != null) {

					questionList = qDao
							.listQuestionsBySurvey(ans.getSurveyId());
					metrics = metricDao.listMetrics(null, null, null,
							l.getOrganization(), "all");
					mappings = metricMappingDao.listMappingsBySurvey(ans
							.getSurveyId());
					loadedItems = true;
				}
				SurveyalValue val = new SurveyalValue();
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

}
