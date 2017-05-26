/*
 *  Copyright (C) 2010-2012, 2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.analytics.dao;

import static com.gallatinsystems.common.util.MemCacheUtils.containsKey;
import static com.gallatinsystems.common.util.MemCacheUtils.putObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;

import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.common.util.MemCacheUtils;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * updates survey question objects
 *
 * @author Christopher Fagiani
 */
public class SurveyQuestionSummaryDao extends BaseDAO<SurveyQuestionSummary> {

    private Cache cache;

    public SurveyQuestionSummaryDao() {
        super(SurveyQuestionSummary.class);
        cache = MemCacheUtils.initCache(4 * 60 * 60); // cache summary objects list for 4 hours
    }

    /**
     * synchronized static method so that only 1 thread can be updating a summary at a time. This is
     * inefficient but is the only way we can be sure we're keeping the count consistent since there
     * is no "select for update" or sql dml-like construct
     *
     * @param answer
     */
    @SuppressWarnings("rawtypes")
    public static synchronized void incrementCount(QuestionAnswerStore answer,
            int unit) {
        PersistenceManager pm = PersistenceFilter.getManager();
        String answerText = answer.getValue();
        String[] answers;
        if (answerText != null && answerText.contains("|")) {
            answers = answerText.split("\\|");
        } else {
            answers = new String[] {
                    answerText
            };
        }
        for (int i = 0; i < answers.length; i++) {
            // find surveyQuestionSummary objects with the right question id and answer text
            javax.jdo.Query query = pm.newQuery(SurveyQuestionSummary.class);
            query
                    .setFilter("questionId == questionIdParam && response == answerParam");
            query
                    .declareParameters("String questionIdParam, String answerParam");
            List results = (List) query.execute(answer.getQuestionID(),
                    answers[i]);
            SurveyQuestionSummary summary = null;
            if ((results == null || results.size() == 0) && unit > 0) {
                // no previous surveyQuestionSummary for this answer, make a new one
                summary = new SurveyQuestionSummary();
                summary.setCount(new Long(unit));
                summary.setQuestionId(answer.getQuestionID());
                summary.setResponse(answers[i]);
            } else if (results != null && results.size() > 0) {
                // update an existing questionAnswerSummary
                summary = (SurveyQuestionSummary) results.get(0);
                summary.setCount(summary.getCount() + unit);
            }
            if (summary != null) {
                // if we have updated or created a surveyQuestionSummary, save it
                SurveyQuestionSummaryDao summaryDao = new SurveyQuestionSummaryDao();
                if (summary.getCount() > 0) {
                    summaryDao.save(summary);
                } else if (summary.getKey() != null) {
                    // if count has been decremented to 0 and the object is
                    // already persisted, delete it
                    summaryDao.delete(summary);
                }
            }
        }
    }

    /**
     * this method will list all the summary objects for a given question id
     */
    public List<SurveyQuestionSummary> listByQuestion(String qId) {
        return listByProperty("questionId", qId, "String");
    }

    /**
     * Retrieve the survey question summary object by response. If possible retrieve from cache
     *
     * @param questionId
     * @param questionResponse
     * @return
     */
    public List<SurveyQuestionSummary> listByResponse(String questionId, String questionResponse) {
        List<SurveyQuestionSummary> result = null;
        String cacheKey = null;
        try {
            cacheKey = getCacheKey(questionId + "-" + questionResponse);
            if (MemCacheUtils.containsKey(cache, cacheKey)) { //let's try to get it
                SurveyQuestionSummary sqs = (SurveyQuestionSummary)cache.get(cacheKey);
                if (sqs != null) {
                    result = new ArrayList<SurveyQuestionSummary>();
                    result.add(sqs);
                    return result;
                }
            }
        } catch (CacheException e) {
            log.log(Level.WARNING, e.getMessage());
        }

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyQuestionSummary.class);

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = null;
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("questionId", filterString, paramString, "String", questionId, paramMap);
        appendNonNullParam("response", filterString, paramString, "String", questionResponse,
                paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        result = (List<SurveyQuestionSummary>) query.executeWithMap(paramMap);
        cache(result);

        return result;
    }

    /**
     * Add a collection of SurveyQuestionSummary objects to the cache. If the object already exists
     * in the cached SurveyQuestionSummarys list, they are replaced by the ones passed in through
     * this list
     *
     * @param summaryList
     */
    private void cache(List<SurveyQuestionSummary> summaryList) {
        if (summaryList == null || summaryList.isEmpty()) {
            return;
        }

        Map<Object, Object> cacheMap = new HashMap<Object, Object>();
        for (SurveyQuestionSummary summary : summaryList) {
            if (summary == null) {
                continue;
            }
            String cacheKey;
            try {
                cacheKey = getCacheKey(summary);
                cacheMap.put(cacheKey, summary);
            } catch (CacheException e) {
                log.log(Level.WARNING, e.getMessage());
            }
        }

        putObjects(cache, cacheMap);
    }

    /**
     * Remove a collection of SurveyQuestionSummarys from the cache
     *
     * @param summaryList
     */
    private void uncache(List<SurveyQuestionSummary> summaryList) {
        if (summaryList == null || summaryList.isEmpty()) {
            return;
        }

        for (SurveyQuestionSummary summary : summaryList) {
            if (summary == null) {
                continue;
            }
            String cacheKey;
            try {
                cacheKey = getCacheKey(summary);
                if (containsKey(cache, cacheKey)) {
                    cache.remove(cacheKey);
                }
            } catch (CacheException e) {
                log.log(Level.WARNING, e.getMessage());
            }
        }
    }

    /**
     * Save and cache question summary
     *
     * @param summary
     */
    public SurveyQuestionSummary save(SurveyQuestionSummary summary) {
        SurveyQuestionSummary savedSummary = super.save(summary);
        cache(Arrays.asList(savedSummary));
        return savedSummary;
    }

    /**
     * Save and cache question summary list
     *
     * @param summary
     */
    public List<SurveyQuestionSummary> save(List<SurveyQuestionSummary> summary) {
        List<SurveyQuestionSummary> savedSummaryList = (List<SurveyQuestionSummary>) super
                .save(summary);
        cache(savedSummaryList);
        return savedSummaryList;
    }

    /**
     * Delete from cache and datastore
     *
     * @param summary
     */
    public void delete(SurveyQuestionSummary summary) {
        uncache(Arrays.asList(summary));
        super.delete(summary);
    }

    /**
     * Delete summary list from cache and datastore
     *
     * @param summaryList
     */
    public void delete(List<SurveyQuestionSummary> summaryList) {
        uncache(summaryList);
        super.delete(summaryList);
    }

    /**
     * Construct cache key for SurveyQuestionSummary objects. Assumes the combination of questionId
     * and questionResponse are unique across all SurveyQuestionSummary entities.
     *
     * @param summary
     * @return
     * @throws CacheException
     */
    @Override
    public String getCacheKey(BaseDomain object) throws CacheException {
        SurveyQuestionSummary summary = (SurveyQuestionSummary) object;
        if (summary.getQuestionId() == null || summary.getResponse() == null) {
            throw new CacheException("Cannnot create cache key without questionId and response");
        }
        return summary.getClass().getSimpleName() + "-" + summary.getQuestionId() + "-"
                + summary.getResponse();
    }
}
