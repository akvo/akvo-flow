/*
 *  Copyright (C) 2010-2019 Stichting Akvo (Akvo Foundation)
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.common.Constants;
import net.sf.jsr107cache.Cache;

import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.common.util.MemCacheUtils;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.gis.geography.dao.CountryDao;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
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

    private static final Logger log = Logger
            .getLogger(SurveyalRestServlet.class.getName());

    private SurveyInstanceDAO surveyInstanceDao;
    private SurveyedLocaleDao surveyedLocaleDao;
    private CountryDao countryDao;
    private String statusFragment;
    private Map<String, String> scoredVals;

    /**
     * initializes the servlet by instantiating all needed Dao classes and loading properties from
     * the configuration.
     */
    public SurveyalRestServlet() {
        surveyInstanceDao = new SurveyInstanceDAO();
        surveyedLocaleDao = new SurveyedLocaleDao();
        countryDao = new CountryDao();
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
     */
    private void ingestSurveyInstance(SurveyInstance surveyInstance) {
        Boolean adaptClusterData = Boolean.FALSE;

        SurveyedLocale locale = surveyedLocaleDao.getByKey(surveyInstance
                    .getSurveyedLocaleId());
        if (locale == null) {
            // We must have a valid locale at this point
            throw new IllegalStateException("Cannot find SurveyedLocale "
                    + "for SurveyInstance " + surveyInstance.toString());
        }

        GeoPlace geoPlace = null;
        Double latitude;
        Double longitude;
        Map<String, Object> geoLocationMap = null;

        try {
            geoLocationMap = SurveyInstance.retrieveGeoLocation(surveyInstance);
        } catch (NumberFormatException nfe) {
            log.log(Level.SEVERE,
                    "Could not parse lat/lon for SurveyInstance "
                            + surveyInstance.getKey().getId());
        }

        if (geoLocationMap != null && !geoLocationMap.isEmpty()) {
            latitude = (Double) geoLocationMap.get(Constants.LATITUDE);
            longitude = (Double) geoLocationMap.get(Constants.LONGITUDE);

            if (!latitude.equals(locale.getLatitude()) || !longitude.equals(locale.getLongitude())
                    || locale.getGeocells() == null || locale.getGeocells().isEmpty()) {
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
        }

        // add surveyInstanceId to list of contributed surveyInstances
        locale.addContributingSurveyInstance(surveyInstance.getKey().getId());

        // last update of the locale information
        locale.setLastSurveyedDate(new Date(surveyInstance.getCollectionDate().getTime()));
        locale.setLastSurveyalInstanceId(surveyInstance.getKey().getId());

        log.log(Level.FINE, "SurveyLocale at this point " + locale.toString());
        final SurveyedLocale savedLocale = surveyedLocaleDao.save(locale);

        // save the surveyalValues
        if (savedLocale.getKey() != null) {
            surveyInstance.setSurveyedLocaleId(savedLocale.getKey().getId());
            surveyedLocaleDao.save(savedLocale);
            surveyInstanceDao.save(surveyInstance);
        }
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
        }
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
