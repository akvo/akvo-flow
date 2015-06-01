/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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

package org.akvo.gae.remoteapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

import static org.akvo.gae.remoteapi.DataUtils.*;

/**
 * This is a data modification script to rectify anomalies with survey instances in monitored
 * groups. It resets the surveyedLocaleId of monitoring form survey instances to point to the same
 * locale created by the registration form with which the monitoring instance shares a
 * survyedLocaleIdentifier
 */
public class CorrectSurveyedLocale implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        if (args.length == 0) {
            System.err
                    .println("Usage: "
                            + RemoteAPI.class.getName()
                            + "CorrectSurveyedLocale <appid> <username> <password> <appid> <surveyId>");
            System.exit(1);
        }

        // retrieve registration form id for surveygroup matching monitored group id passed in
        Long surveyId = Long.parseLong(args[0]);
        Entity monitoringSurvey = retrieveMonitoringSurvey(ds, surveyId);
        List<Entity> surveyForms = retrieveSurveyForms(ds, surveyId);

        // map locale identifiers and surveyedlocale ids for all survey instances created by
        // registration form
        Long registrationFormId = (Long) monitoringSurvey.getProperty("newLocaleSurveyId");

        if (registrationFormId == null) {
            System.err.println("Unable to identify registration form for survey: " + surveyId);
            System.exit(1);
        }

        // retrieve all monitoring surveys for survey group
        List<Entity> dataPoints = retrieveSurveyInstances(ds, registrationFormId);
        Map<String, Long> surveyedLocaleDeviceIdentifierMap = mapDeviceIdentifierSurveyedLocaleId(dataPoints);

        // for each monitoring survey
        for (Entity monitoringForm : surveyForms) {
            Long formId = new Long(monitoringForm.getKey().getId());
            if (registrationFormId.equals(formId)) {
                continue; // skip registration form processing
            }

            List<Entity> monitoringFormInstances = retrieveSurveyInstances(ds, formId);
            List<Entity> correctedMonitoringFormInstances = correctSurveyedLocale(
                    monitoringFormInstances, surveyedLocaleDeviceIdentifierMap, ds);
            batchSaveEntities(ds, correctedMonitoringFormInstances);
        }
    }

    /**
     * Retrieve the monitoring survey entity
     *
     * @param ds
     * @param surveyId
     * @return
     * @throws EntityNotFoundException
     */
    private Entity retrieveMonitoringSurvey(DatastoreService ds, Long surveyId)
            throws EntityNotFoundException {
        return ds.get(KeyFactory.createKey(SURVEY_KIND, surveyId));
    }

    /**
     * Retrieve all forms belonging to the monitoring survey
     *
     * @param ds
     * @param surveyId
     * @return
     */
    private List<Entity> retrieveSurveyForms(DatastoreService ds, Long surveyId) {
        Filter surveyFilter = new Query.FilterPredicate("surveyGroupId", FilterOperator.EQUAL,
                surveyId);
        Query q = new Query(FORM_KIND).setFilter(surveyFilter);
        return ds.prepare(q).asList(FetchOptions.Builder.withDefaults());
    }

    /**
     * Retrieve all form instances (responses) linked to a form Id.
     *
     * @param ds
     * @param formId
     * @return
     */
    private List<Entity> retrieveSurveyInstances(DatastoreService ds, Long formId) {
        Filter formFilter = new Query.FilterPredicate("surveyId", FilterOperator.EQUAL,
                formId);
        Query q = new Query(FORM_INSTANCE_KIND).setFilter(formFilter);

        List<Entity> formInstances = new ArrayList<Entity>();
        for (Entity formInstance : ds.prepare(q).asIterable()) {
            formInstances.add(formInstance);
        }

        return formInstances;
    }

    /**
     * Create a mapping between the identifier and the data point id (surveyedLocaleId) of
     * registration form responses
     *
     * @param dataPoints
     * @return
     */
    private Map<String, Long> mapDeviceIdentifierSurveyedLocaleId(List<Entity> dataPoints) {
        Map<String, Long> dataPointIdentifiersMap = new HashMap<String, Long>();
        for (Entity dataPoint : dataPoints) {
            String identifier = (String) dataPoint.getProperty(DATA_POINT_STRING_ID);
            Long dataPointId = (Long) dataPoint.getProperty(DATA_POINT_NUMERICAL_ID);
            if (identifier != null && dataPointId != null) {
                dataPointIdentifiersMap.put(identifier, dataPointId);
            } else {
                System.out.println(dataPoint.getKey() + " missing identifier (" + identifier
                        + ") or surveyedLocaleId (" + dataPointId + ")");
            }
        }
        return dataPointIdentifiersMap;
    }

    /**
     * Reset the surveyedLocaleId for instances with matching identifiers and non-matching surveyed
     * locale ids
     *
     * @param monitoringFormInstances
     * @param surveyedLocaleDeviceIdentifierMap
     * @param ds TODO
     * @return
     */
    private List<Entity> correctSurveyedLocale(List<Entity> monitoringFormInstances,
            Map<String, Long> surveyedLocaleDeviceIdentifierMap, DatastoreService ds) {
        List<Entity> correctedFormInstances = new ArrayList<Entity>();
        List<Long> oldSurveyedLocaleIds = new ArrayList<Long>();

        for (Entity formInstance : monitoringFormInstances) {
            String identifier = (String) formInstance.getProperty(DATA_POINT_STRING_ID);
            Long surveyedLocaleId = (Long) formInstance.getProperty(DATA_POINT_NUMERICAL_ID);

            if (identifier == null || !surveyedLocaleDeviceIdentifierMap.containsKey(identifier)) {
                System.out.println(formInstance.getKey() + " missing identifier");
                continue;
            }

            Long dataPointId = surveyedLocaleDeviceIdentifierMap.get(identifier);
            if (!dataPointId.equals(surveyedLocaleId)) {
                formInstance.setProperty(DATA_POINT_NUMERICAL_ID, dataPointId);
                correctedFormInstances.add(formInstance);
                System.out.println("Changing " + formInstance.getKey() + ";" + identifier + ";"
                        + surveyedLocaleId + "=>"
                        + dataPointId);
                oldSurveyedLocaleIds.add(surveyedLocaleId);
            }
        }

        batchDelete(ds, oldSurveyedLocaleIds, DATA_POINT_KIND);
        return correctedFormInstances;
    }
}
