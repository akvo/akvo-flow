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

import org.apache.commons.lang.StringUtils;
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
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * RESTFul servlet that can handle handle operations on SurveyedLocale and related domain objects.
 * TODO: consider storing survey question list, metrics and mappings in a Soft-Reference map to
 * speed up processing.
 *
 * @author Christopher Fagiani
 */
public class SurveyalRestServlet extends AbstractRestApiServlet {
    private static final long serialVersionUID = 5923399458369692813L;
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
    private String statusFragment;
    private Map<String, String> scoredVals;

    /**
     * initializes the servlet by instantiating all needed Dao classes and loading properties from
     * the configuration.
     */
    public SurveyalRestServlet() {
        surveyInstanceDao = new SurveyInstanceDAO();
        surveyedLocaleDao = new SurveyedLocaleDao();
        qDao = new QuestionDao();
        countryDao = new CountryDao();
        metricDao = new MetricDao();
        metricMappingDao = new SurveyMetricMappingDao();
        // TODO: once the appropriate metric types are defined and reliably
        // assigned, consider removing this in favor of metrics
        statusFragment = PropertyUtil.getProperty("statusQuestionText");
        if (statusFragment != null && statusFragment.trim().length() > 0) {
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
            Boolean decrement = sReq.getDecrementClusterCount();
            int delta = decrement ? -1 : 1; // increment by default
            adaptClusterData(sReq.getSurveyedLocaleId(), delta);
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
     * Create or update a surveyedLocale based on the Geo data that is retrieved from a
     * surveyInstance. This method is unlikely to run in under 1 minute (based on datastore latency)
     * so it is best invoked via a task queue
     *
     * @param surveyInstanceId
     */
    private void ingestSurveyInstance(SurveyInstance surveyInstance) {
        SurveyedLocale locale = null;
        Boolean adaptClusterData = Boolean.FALSE;

        // if the surveyed locale id was available in the ingested data,
        // this has been set in the save method in surveyInstanceDao.
        if (surveyInstance.getSurveyedLocaleId() != null) {
            locale = surveyedLocaleDao.getByKey(surveyInstance
                    .getSurveyedLocaleId());
        }

        // create a new locale with basic information
        if (locale == null) {
            // we don't have a locale
            locale = new SurveyedLocale();

            if (StringUtils.isNotBlank(surveyInstance
                    .getSurveyedLocaleIdentifier())) {
                locale.setIdentifier(surveyInstance.getSurveyedLocaleIdentifier());
            } else {
                // if we don't have an identifier, create a random UUID.
                locale.setIdentifier(SurveyedLocale.generateBase32Uuid());
            }

            locale.setOrganization(PropertyUtil
                    .getProperty(DEFAULT_ORG_PROP));

            Survey survey = SurveyUtils.retrieveSurvey(surveyInstance.getSurveyId());
            if (survey != null) {
                locale.setLocaleType(survey.getPointType());
                locale.setSurveyGroupId(survey.getSurveyGroupId());
                locale.setCreationSurveyId(survey.getKey().getId());
            }
        }

        // try to construct geoPlace. Geo information can come from two sources:
        // 1) the META_GEO information in the surveyInstance, and
        // 2) a geo question.
        // If we can't find geo information in 1), we try 2)

        GeoPlace geoPlace = null;
        Double latitude = UNSET_VAL;
        Double longitude = UNSET_VAL;
        Map<String, Object> geoLocationMap = null;

        try {
            geoLocationMap = SurveyInstance.retrieveGeoLocation(surveyInstance);
        } catch (NumberFormatException nfe) {
            log.log(Level.SEVERE,
                    "Could not parse lat/lon for SurveyInstance "
                            + surveyInstance.getKey().getId());
        }

        if (geoLocationMap != null && !geoLocationMap.isEmpty()) {
            latitude = (Double) geoLocationMap.get(MapUtils.LATITUDE);
            longitude = (Double) geoLocationMap.get(MapUtils.LONGITUDE);

            if (!latitude.equals(locale.getLatitude())
                    || !longitude.equals(locale.getLongitude())) {
                locale.setLatitude(latitude);
                locale.setLongitude(longitude);
                try {
                    locale.setGeocells(GeocellManager
                            .generateGeoCell(new Point(latitude, longitude)));
                } catch (Exception ex) {
                    log.log(Level.INFO, "Could not generate Geocell for locale: "
                            + locale.getKey().getId() + " error: " + ex);
                }
                adaptClusterData = Boolean.TRUE;
            }

            geoPlace = getGeoPlace(latitude, longitude);
        }

        if (geoPlace != null) {

            // if we have geoinformation, we will use it on the locale provided that:
            // 1) it is a new Locale, or 2) it was brought in as meta information, meaning it should
            // overwrite previous locale geo information
            setGeoData(geoPlace, locale);

            // TODO: move this to survey instance processing logic
            // if we have a geoPlace, set it on the instance
            surveyInstance.setCountryCode(geoPlace.getCountryCode());
            surveyInstance.setSublevel1(geoPlace.getSub1());
            surveyInstance.setSublevel2(geoPlace.getSub2());
            surveyInstance.setSublevel3(geoPlace.getSub3());
            surveyInstance.setSublevel4(geoPlace.getSub4());
            surveyInstance.setSublevel5(geoPlace.getSub5());
            surveyInstance.setSublevel6(geoPlace.getSub6());
        }

        if (StringUtils.isNotBlank(surveyInstance
                .getSurveyedLocaleDisplayName())) {
            locale.setDisplayName(surveyInstance.getSurveyedLocaleDisplayName());
        }

        // add surveyInstanceId to list of contributed surveyInstances
        locale.addContributingSurveyInstance(surveyInstance.getKey().getId());

        // last update of the locale information
        locale.setLastSurveyedDate(surveyInstance.getCollectionDate());
        locale.setLastSurveyalInstanceId(surveyInstance.getKey().getId());

        log.log(Level.FINE, "SurveyLocale at this point " + locale.toString());
        final SurveyedLocale savedLocale = surveyedLocaleDao.save(locale);

        // save the surveyalValues
        if (savedLocale.getKey() != null) {
            surveyInstance.setSurveyedLocaleId(savedLocale.getKey().getId());
            List<SurveyalValue> values = constructValues(savedLocale);
            if (values != null) {
                surveyedLocaleDao.save(values);
            }
            surveyedLocaleDao.save(savedLocale);
            surveyInstanceDao.save(surveyInstance);
        }

        // finally fire off adapt cluster data task
        // TODO: consider firing this task after ALL survey instances are processed
        // instead of a single survey instance
        // TODO: when surveyedLocales are deleted, it needs to be substracted from the clusters
        if (adaptClusterData) {
            Queue defaultQueue = QueueFactory.getDefaultQueue();
            TaskOptions adaptClusterTaskOptions = TaskOptions.Builder
                    .withUrl("/app_worker/surveyalservlet")
                    .param(SurveyalRestRequest.ACTION_PARAM,
                            SurveyalRestRequest.ADAPT_CLUSTER_DATA_ACTION)
                    .param(SurveyalRestRequest.SURVEYED_LOCALE_PARAM,
                            Long.toString(locale.getKey().getId()));
            defaultQueue.add(adaptClusterTaskOptions);
        }
    }

    // this method is synchronised, because we are changing counts.
    private synchronized void adaptClusterData(Long surveyedLocaleId, Integer delta) {
        final SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        final SurveyedLocale locale = slDao.getById(surveyedLocaleId);

        if (locale == null) {
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
            if (delta < 0) {
                to.param(SurveyalRestRequest.DECREMENT_CLUSTER_COUNT_PARAM,
                        Boolean.TRUE.toString());
            }
            queue.add(to);
            return;
        }

        MapUtils.recomputeCluster(cache, locale, delta);
    }

    /**
     * tries several methods to resolve the lat/lon to a GeoPlace. If a geoPlace is found, looks for
     * the country in the database and creates it if not found
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
     * uses the geolocationService to determine the geographic sub-regions and country for a given
     * point
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
     * converts QuestionAnswerStore objects into SurveyalValues, copying the overlapping values from
     * SurveyedLocale as needed. The surveydLocale must have been saved prior to calling this method
     * if one expects the surveyedLocaleId member to be populated.
     *
     * @param l
     * @param answers
     * @return
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private List<SurveyalValue> constructValues(SurveyedLocale l) {
        List<QuestionAnswerStore> answers = surveyInstanceDao.listQuestionAnswerStore(
                l.getLastSurveyalInstanceId(), null);
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
            Map qMap = new HashMap<Long, Integer>();
            Integer index = 0;
            if (questionList != null) {
                for (Question qu : questionList) {
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
                if (ans.getQuestionID() != null) {
                    try {
                        questionId = Long.parseLong(ans.getQuestionID());
                    } catch (NumberFormatException e) {
                        log.log(Level.SEVERE,
                                "Could not create surveyal value for question answer: "
                                        + ans.getKey().getId() + ": "
                                        + "can't parse questionId.");
                    }
                }

                if (questionId != null &&
                        qMap.containsKey(questionId)) {
                    q = questionList.get((Integer) qMap.get(questionId));
                    val.setQuestionText(q.getText());
                    val.setSurveyQuestionId(q.getKey().getId());
                    val.setQuestionType(q.getType().toString());
                    val.setQuestionOrder(q.getOrder());
                    val.setSurveyId(q.getSurveyId());

                    // try to get question group order from cache
                    key = "qg-order-" + q.getQuestionGroupId();
                    if (cache != null && cache.containsKey(key)) {
                        questionGroupOrder = (Integer) cache.get(key);
                    } else {
                        // if not in cache, find it in datastore
                        QuestionGroup qg = qgDao.getByKey(q.getQuestionGroupId());
                        if (qg != null) {
                            questionGroupOrder = qg.getOrder();
                            if (cache != null) {
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
     * runs over all surveydLocale objects, and populates: the Geocells field based on the latitude
     * and longitude. New surveyedLocales will have these fields populated automatically, this
     * method is to update legacy data. This method is invoked as a URL request:
     * http://..../rest/actions?action=populateGeocellsForLocale
     *
     * @param cursor
     */
    private void populateGeocellsForLocale(String cursor) {
        log.log(Level.INFO, "Populating geocells for locales");
        SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        List<SurveyedLocale> surveyedLocaleList = slDao.list(cursor);
        String newCursor = SurveyedLocaleDao.getCursor(surveyedLocaleList);

        if (surveyedLocaleList == null || surveyedLocaleList.size() == 0) {
            log.log(Level.INFO, "No locales found");
            return;
        }

        for (SurveyedLocale sl : surveyedLocaleList) {
            if (sl.getGeocells() != null && sl.getGeocells().size() > 0) {
                continue;
            }

            if (sl.getLatitude() == null && sl.getLongitude() == null) {
                log.log(Level.INFO, "Could not populate Geocells for SurveyedLocale: "
                        + sl.getKey().getId() + ". No lat/lon values set");
                continue;
            }

            // populate geocells
            try {
                sl.setGeocells(GeocellManager.generateGeoCell(new Point(sl.getLatitude(),
                        sl.getLongitude())));
            } catch (Exception ex) {
                log.log(Level.INFO, "Could not generate Geocell for SurveyedLocale: "
                        + sl.getKey().getId() + " error: " + ex);
            }
            slDao.save(sl);
        }

        // launch task for remaining locales
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder
                .withUrl("/app_worker/surveyalservlet")
                .param(SurveyalRestRequest.ACTION_PARAM,
                        SurveyalRestRequest.POP_GEOCELLS_FOR_LOCALE_ACTION)
                .param("cursor", newCursor));
    }
}
