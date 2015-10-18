/*
 *  Copyright (C) 2010-2015 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.dao;

import static com.gallatinsystems.common.util.MemCacheUtils.containsKey;
import static com.gallatinsystems.common.util.MemCacheUtils.initCache;
import static com.gallatinsystems.common.util.MemCacheUtils.putObjects;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;

import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

public class QuestionAnswerStoreDao extends BaseDAO<QuestionAnswerStore> {

    private Cache cache;

    public QuestionAnswerStoreDao() {
        super(QuestionAnswerStore.class);
        cache = initCache(4 * 60 * 60); // cache questions list for 4 hours
    }

    public List<QuestionAnswerStore> listBySurvey(Long surveyId) {
        return super.listByProperty("surveyId", surveyId, "Long");

    }

    public List<QuestionAnswerStore> listByQuestion(Long questionId) {
        return super.listByProperty("questionID", questionId.toString(),
                "String", "createdDateTime");
    }

    @SuppressWarnings("unchecked")
    public List<QuestionAnswerStore> listByQuestion(Long questionId, String cursor, Integer pageSize) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(QuestionAnswerStore.class);

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        appendNonNullParam("questionID", filterString, paramString, "String",
                String.valueOf(questionId),
                paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        query.setOrdering("createdDateTime");
        prepareCursor(cursor, pageSize, query);
        return (List<QuestionAnswerStore>) query.executeWithMap(paramMap);
    }

    /**
     * lists all the QuestionAnswerStore objects that match the type passed in
     *
     * @param type
     * @param cursor
     * @param pageSize
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<QuestionAnswerStore> listByTypeAndDate(String type,
            Long surveyId, Date sinceDate, String cursor, Integer pageSize) {

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(QuestionAnswerStore.class);

        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("type", filterString, paramString, "String", type,
                paramMap);
        appendNonNullParam("surveyId", filterString, paramString, "Long",
                surveyId, paramMap);
        appendNonNullParam("lastUpdateDateTime", filterString, paramString,
                "Date", sinceDate, paramMap, GTE_OP);
        if (sinceDate != null) {
            query.declareImports("import java.util.Date");
        }
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        prepareCursor(cursor, pageSize, query);
        return (List<QuestionAnswerStore>) query.executeWithMap(paramMap);
    }

    @SuppressWarnings("unchecked")
    public List<QuestionAnswerStore> listByTypeValue(String type, String value) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(QuestionAnswerStore.class);

        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("type", filterString, paramString, "String", type,
                paramMap);
        appendNonNullParam("value", filterString, paramString, "String", value,
                paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        return (List<QuestionAnswerStore>) query.executeWithMap(paramMap);
    }

    /**
     * lists all the QuestionAnswerStore objects that match the type passed in
     *
     * @param sinceDate
     * @param cursor
     * @param pageSize
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<QuestionAnswerStore> listByNotNullCollectionDateBefore(
            Date sinceDate, String cursor, Integer pageSize) {

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(QuestionAnswerStore.class);

        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();
        appendNonNullParam("collectionDate", filterString, paramString, "Date",
                sinceDate, paramMap, LTE_OP);
        if (sinceDate != null) {
            query.declareImports("import java.util.Date");
        }
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        prepareCursor(cursor, pageSize, query);
        return (List<QuestionAnswerStore>) query.executeWithMap(paramMap);
    }

    @SuppressWarnings("unchecked")
    public List<QuestionAnswerStore> listByNotNullCollectionDateAfter(
            Date sinceDate, String cursor, Integer pageSize) {

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(QuestionAnswerStore.class);

        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();
        appendNonNullParam("collectionDate", filterString, paramString, "Date",
                sinceDate, paramMap, GTE_OP);
        if (sinceDate != null) {
            query.declareImports("import java.util.Date");
        }
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        // prepareCursor(cursor, pageSize, query);
        log.log(Level.INFO, query.toString());
        return (List<QuestionAnswerStore>) query.executeWithMap(paramMap);
    }

    /**
     * lists all the QuestionAnswerStore objects that match the type passed in
     *
     * @param sinceDate
     * @param cursor
     * @param pageSize
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<QuestionAnswerStore> listByExactDateString() {

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery("select from "
                + QuestionAnswerStore.class.getName()
                + " where collectionDate == 5674906531303000000");
        log.log(Level.INFO, query.toString());
        return (List<QuestionAnswerStore>) query.execute();
    }

    public List<QuestionAnswerStore> listBySurveyInstance(Long surveyInstanceId) {
        List<QuestionAnswerStore> responses = super.listByProperty("surveyInstanceId",
                surveyInstanceId, "Long");
        cache(responses);
        return responses;
    }

    public Map<Long, Map<Integer, QuestionAnswerStore>> mapByQuestionIdAndIteration(
            List<QuestionAnswerStore> qasList) {

        Map<Long, Map<Integer, QuestionAnswerStore>> responseMap = new HashMap<>();

        for (QuestionAnswerStore a : qasList) {
            Long questionId = Long.parseLong(a.getQuestionID());
            Integer iteration = a.getIteration();
            // default iteration = 0
            iteration = iteration == null ? 0 : iteration;

            Map<Integer, QuestionAnswerStore> iterationMap = responseMap.get(questionId);

            if (iterationMap == null) {
                iterationMap = new HashMap<>();
            }
            iterationMap.put(iteration, a);
            responseMap.put(questionId, iterationMap);
        }

        return responseMap;
    }

    public Map<Long, QuestionAnswerStore> mapByQuestionId(List<QuestionAnswerStore> qasList) {
        Map<Long, QuestionAnswerStore> qasMap = new HashMap<Long, QuestionAnswerStore>();
        try {
            for (QuestionAnswerStore a : qasList) {
                if (a.getQuestionID() != null) {
                    qasMap.put(Long.parseLong(a.getQuestionID()), a);
                }
            }
            return qasMap;
        } catch (NumberFormatException e) {
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    public QuestionAnswerStore getByQuestionAndSurveyInstance(Long questionId, Long surveyInstanceId) {
        String cacheKey;
        try {
            cacheKey = getCacheKey(surveyInstanceId + "-" + questionId);
            if (containsKey(cache, cacheKey)) {
                return (QuestionAnswerStore) cache.get(cacheKey);
            }
        } catch (CacheException e) {
            log.log(Level.WARNING, e.getMessage());
        }

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(QuestionAnswerStore.class);
        query.setFilter("surveyInstanceId == surveyInstanceIdParam && questionID == questionIdParam");
        query.declareParameters("Long surveyInstanceIdParam, String questionIdParam");
        List<QuestionAnswerStore> results = (List<QuestionAnswerStore>) query.execute(
                surveyInstanceId, questionId.toString());
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public boolean isCached(Long questionId, Long surveyInstanceId) {
        try {
            return containsKey(cache, getCacheKey(surveyInstanceId + "-" + questionId));
        } catch (CacheException e) {
            // ignore
        }
        return false;
    }

    /**
     * Saves response and update cache
     *
     * @param reponse
     */
    public QuestionAnswerStore save(QuestionAnswerStore reponse) {
        // first save and get Id
        QuestionAnswerStore savedResponse = super.save(reponse);
        cache(Arrays.asList(savedResponse));
        return savedResponse;
    }

    /**
     * Save a collection of responses and cache
     *
     * @param responseList
     * @return
     */
    public List<QuestionAnswerStore> save(List<QuestionAnswerStore> responseList) {
        List<QuestionAnswerStore> savedResponses = (List<QuestionAnswerStore>) super
                .save(responseList);
        cache(savedResponses);
        return savedResponses;
    }

    /**
     * Delete from cache and datastore
     *
     * @param response
     */
    public void delete(QuestionAnswerStore response) {
        uncache(Arrays.asList(response));
        super.delete(response);
    }

    /**
     * Delete response list from cache and datastore
     *
     * @param responsesList
     */
    public void delete(List<QuestionAnswerStore> responsesList) {
        uncache(responsesList);
        super.delete(responsesList);
    }

    /**
     * Add a collection of QuestionAnswerStore objects to the cache
     *
     * @param responseList
     */
    private void cache(List<QuestionAnswerStore> responseList) {
        if (responseList == null || responseList.isEmpty()) {
            return;
        }

        Map<Object, Object> cacheMap = new HashMap<Object, Object>();
        for (QuestionAnswerStore response : responseList) {
            if (response == null) {
                continue;
            }
            String cacheKey = null;
            try {
                cacheKey = getCacheKey(response);
                cacheMap.put(cacheKey, response);
            } catch (CacheException e) {
                log.log(Level.WARNING, e.getMessage());
            }
        }

        putObjects(cache, cacheMap);
    }

    /**
     * Remove a collection of responses from the cache
     *
     * @param responsesList
     */
    private void uncache(List<QuestionAnswerStore> responsesList) {
        if (responsesList == null || responsesList.isEmpty()) {
            return;
        }

        for (QuestionAnswerStore response : responsesList) {
            if (response == null) {
                continue;
            }
            String cacheKey = null;
            try {
                cacheKey = getCacheKey(response);
                if (containsKey(cache, cacheKey)) {
                    cache.remove(cacheKey);
                }
            } catch (CacheException e) {
                log.log(Level.WARNING, e.getMessage());
            }
        }
    }

    /**
     * Construct cache key for QuestionAnswerStore objects. Assumes the combination of
     * surveyInstanceId and questionId are unique across all QuestionAnswerStore entities.
     *
     * @param response
     * @return
     * @throws CacheException
     */
    @Override
    public String getCacheKey(BaseDomain object) throws CacheException {
        QuestionAnswerStore response = (QuestionAnswerStore) object;
        if (response.getSurveyInstanceId() == null || response.getQuestionID() == null) {
            throw new CacheException(
                    "Cannnot create cache key without surveyInstanceId and questionId");
        }
        return response.getClass().getSimpleName() + "-" + response.getSurveyInstanceId() + "-"
                + response.getQuestionID();
    }
}
