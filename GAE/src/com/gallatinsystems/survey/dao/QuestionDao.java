/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.dao;

import static com.gallatinsystems.common.util.MemCacheUtils.containsKey;
import static com.gallatinsystems.common.util.MemCacheUtils.initCache;
import static com.gallatinsystems.common.util.MemCacheUtils.putObjects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.NotPersistent;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;

import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.exceptions.IllegalDeletionException;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;
import com.gallatinsystems.surveyal.dao.SurveyalValueDao;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

/**
 * saves/finds question objects
 */
public class QuestionDao extends BaseDAO<Question> {

    private QuestionOptionDao optionDao;
    private QuestionHelpMediaDao helpDao;
    private TranslationDao translationDao;
    private ScoringRuleDao scoringRuleDao;
    private Cache cache;

    public QuestionDao() {
        super(Question.class);
        optionDao = new QuestionOptionDao();
        helpDao = new QuestionHelpMediaDao();
        translationDao = new TranslationDao();
        scoringRuleDao = new ScoringRuleDao();
        cache = initCache(4 * 60 * 60); // cache questions list for 4 hours
    }


    /**
     * loads the Question object but NOT any associated options
     *
     * @param id
     * @return
     */
    public Question getQuestionHeader(Long id) {
        return getByKey(id);

    }

    /**
     * lists minimal question information by surveyId
     *
     * @param surveyId
     * @return
     */
    public List<Question> listQuestionsBySurvey(Long surveyId) {
        List<Question> questionsList = listByProperty("surveyId", surveyId,
                "Long", "order", "asc");

        if (questionsList == null) {
            return Collections.emptyList();
        }

        cache(questionsList);

        return questionsList;
    }

    /**
     * Delete a list of questions
     *
     * @param qList
     */
    public void delete(List<Question> qList) {
        uncache(qList);
        super.delete(qList);
    }

    /**
     * Delete question from data store.
     *
     * @param question
     */
    public void delete(Question question) throws IllegalDeletionException {
        delete(question, Boolean.TRUE);
    }

    /**
     * Delete a question and adjust the question order for the remaining questions if specified
     *
     * @param question
     * @param adjustQuestionOrder
     * @throws IllegalDeletionException
     */
    public void delete(Question question, Boolean adjustQuestionOrder)
            throws IllegalDeletionException {
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        SurveyalValueDao svDao = new SurveyalValueDao();
        if (qasDao.listByQuestion(question.getKey().getId()).size() > 0
                || svDao.listByQuestion(question.getKey().getId()).size() > 0) {
            throw new IllegalDeletionException(
                    "Cannot delete question with id "
                            + question.getKey().getId()
                            + " ("
                            + question.getText()
                            + ") because there are already survey responses stored for this question. Please delete all survey responses first.");
        }

        helpDao.deleteHelpMediaForQuestion(question.getKey().getId());

        optionDao.deleteOptionsForQuestion(question.getKey().getId());

        translationDao.deleteTranslationsForParent(question.getKey().getId(),
                Translation.ParentType.QUESTION_TEXT);

        // to use later when adjust question order
        Long deletedQuestionGroupId = question.getQuestionGroupId();
        Integer deletedQuestionOrder = question.getOrder();

        uncache(Arrays.asList(question)); // clear from cached first

        // only delete after extracting group ID and order
        super.delete(question);

        if (adjustQuestionOrder != null && adjustQuestionOrder) {
            // update question order
            TreeMap<Integer, Question> groupQs = listQuestionsByQuestionGroup(
                    deletedQuestionGroupId, false);
            if (groupQs != null) {
                for (Question gq : groupQs.values()) {
                    if (gq.getOrder() >= deletedQuestionOrder) {
                        gq.setOrder(gq.getOrder() - 1);
                    }
                }
            }
        }
    }

    /**
     * Delete all the questions in a group
     *
     * @param surveyId
     * @throws IllegalDeletionException
     */
    public void deleteQuestionsForGroup(Long questionGroupId) throws IllegalDeletionException {
        for (Question q : listQuestionsByQuestionGroup(questionGroupId, Boolean.TRUE).values()) {
            delete(q, Boolean.FALSE);
        }
    }

    /**
     * lists all questions in a group and orders them by their sortOrder
     *
     * @param groupId
     * @return
     */
    public List<Question> listQuestionsInOrderForGroup(Long groupId) {
        return listByProperty("questionGroupId", groupId, "Long", "order",
                "asc");
    }


    /**
     * list questions in order, doing our own sorting to avoid many datastore calls. Optionally filtered by type
     *
     * @param surveyId
     * @return
     */
    public List<Question> listQuestionsInOrder(Long surveyId, Question.Type type) {
        List<Question> orderedQuestionList = new ArrayList<Question>();
        //We must know the order of the question groups
        QuestionGroupDao qgDao = new QuestionGroupDao();
        List<QuestionGroup> orderedGroupList = qgDao.listQuestionGroupBySurvey(surveyId);
        
        Map<Long, List<Question>> idMap = new HashMap<>();
        for (QuestionGroup group : orderedGroupList) {
            List<Question> questions = new ArrayList<>();
            idMap.put(group.getKey().getId(), questions);
        }
        List<Question> unorderedQuestions;
        if (type == null) {
            unorderedQuestions = listByProperty("surveyId", surveyId, "Long");
        } else {
            unorderedQuestions = getBySurveyAndType(surveyId, type);
        }
        
        // Sort them into their respective lists
        for (Question q:unorderedQuestions) {
            List<Question> myList = idMap.get(q.getQuestionGroupId());
            if (myList != null) { //in case db is inconsistent
                myList.add(q);
            }
        }
        // Lists complete, now we can sort them and copy each in order
        for (QuestionGroup group : orderedGroupList) {
            List<Question> questions = idMap.get(group.getKey().getId());
            Collections.sort(questions, new Comparator<Question>() {
                @Override
                public int compare(Question o1, Question o2) {
                    //order should never be null, but accidents happen...
                    int v1 = o1.getOrder() != null ? o1.getOrder() : 0;
                    int v2 = o2.getOrder() != null ? o2.getOrder() : 0;
                    return v1-v2;
                }
            });
            orderedQuestionList.addAll(questions);
        }
        
        return orderedQuestionList;
    }

    /**
     * Lists questions by questionGroupId and type
     *
     * @param questionGroupId
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Question> getByQuestiongroupAndType(long questionGroupId, Question.Type type) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Question.class);
        query.setFilter(" questionGroupId == questionGroupIdParam && type == questionTypeParam");
        query.declareParameters("Long questionGroupIdParam, String questionTypeParam");
        query.setOrdering("order asc");
        List<Question> results = (List<Question>) query.execute(questionGroupId, type.toString());
        if (results != null && results.size() > 0) {
            return results;
        } else {
            return null;
        }
    }

    /**
     * Lists questions by surveyId and type
     *
     * @param surveyId
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Question> getBySurveyAndType(long surveyId, Question.Type type) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Question.class);
        query.setFilter(" surveyId == surveyIdParam && type == questionTypeParam");
        query.declareParameters("Long surveyIdParam, String questionTypeParam");
        List<Question> results = (List<Question>) query.execute(surveyId, type.toString());
        if (results != null && results.size() > 0) {
            return results;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * saves a question object in a transaction
     *
     * @param q
     * @return
     */
    public Question saveTransactional(Question q) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        Entity question = null;
        try {
            if (q.getKey() != null) {
                try {
                    question = datastore.get(q.getKey());
                } catch (Exception e) {
                    log.log(Level.WARNING,
                            "Key is set but not found. Assuming this is an import");
                    question = new Entity(q.getKey());
                }
            } else {
                question = new Entity("Question");
            }

            Field[] f = Question.class.getDeclaredFields();
            for (int i = 0; i < f.length; i++) {
                if (!"key".equals(f[i].getName())
                        && f[i].getAnnotation(NotPersistent.class) == null
                        && !"type".equals(f[i].getName())
                        && !f[i].getName().startsWith("jdo")
                        && !f[i].getName().equals("serialVersionUID")) {
                    f[i].setAccessible(true);
                    question.setProperty(f[i].getName(), f[i].get(q));
                }
            }
            // now set the type
            question.setProperty("type", q.getType().toString());
            // Ensure that createdDateTime and lastUpdateDateTime properties are set
            Date date = new Date();
            if (question.getProperty("createdDateTime") == null) {
                question.setProperty("createdDateTime", date);
            }
            if (question.getProperty("lastUpdateDateTime") == null) {
                question.setProperty("lastUpdateDateTime", date);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not set entity fields", e);
        }

        Key key = datastore.put(question);
        q.setKey(key);
        cache(Arrays.asList(q));
        txn.commit();

        return q;
    }

    /**
     * saves a question, including its question options, translations, and help media (if any).
     *
     * @param question
     * @param questionGroupId
     * @return
     */
    public Question save(Question question, Long questionGroupId) {
        if (questionGroupId != null) {
            question.setQuestionGroupId(questionGroupId);
            QuestionGroup group = getByKey(questionGroupId, QuestionGroup.class);
            if (group != null) {
                question.setSurveyId(group.getSurveyId());
            }
        }
        question = saveTransactional(question);
        // delete existing options

        QuestionOptionDao qoDao = new QuestionOptionDao();
        TreeMap<Integer, QuestionOption> qoMap = qoDao
                .listOptionByQuestion(question.getKey().getId());
        if (qoMap != null) {
            for (Map.Entry<Integer, QuestionOption> entry : qoMap.entrySet()) {
                qoDao.delete(entry.getValue());
            }
        }
        if (question.getQuestionOptionMap() != null) {
            for (QuestionOption opt : question.getQuestionOptionMap().values()) {
                opt.setQuestionId(question.getKey().getId());
                if (opt.getText() != null && opt.getText().contains(",")) {
                    opt.setText(opt.getText().replaceAll(",", "-"));
                    if (opt.getCode() != null) {
                        opt.setCode(opt.getCode().replaceAll(",", "-"));
                    }
                }
                save(opt);
                if (opt.getTranslationMap() != null) {
                    for (Translation t : opt.getTranslationMap().values()) {
                        if (t.getParentId() == null) {
                            t.setParentId(opt.getKey().getId());
                        }
                    }
                    super.save(opt.getTranslationMap().values());
                }
            }
        }
        if (question.getTranslationMap() != null) {
            for (Translation t : question.getTranslationMap().values()) {
                if (t.getParentId() == null) {
                    t.setParentId(question.getKey().getId());
                }
            }
            super.save(question.getTranslationMap().values());
        }

        if (question.getQuestionHelpMediaMap() != null) {
            for (QuestionHelpMedia help : question.getQuestionHelpMediaMap()
                    .values()) {
                help.setQuestionId(question.getKey().getId());

                save(help);
                if (help.getTranslationMap() != null) {
                    for (Translation t : help.getTranslationMap().values()) {
                        if (t.getParentId() == null) {
                            t.setParentId(help.getKey().getId());
                        }
                    }
                    super.save(help.getTranslationMap().values());
                }
            }
        }
        return question;
    }

    /**
     * Saves question and update cache
     *
     * @param question
     */
    public Question save(Question question) {
        // first save and get Id
        Question savedQuestion = super.save(question);
        cache(Arrays.asList(savedQuestion));
        return savedQuestion;
    }

    /**
     * Save a collection of questions and cache
     *
     * @param qList
     * @return
     */
    public List<Question> save(List<Question> qList) {
        List<Question> savedQuestions = (List<Question>) super.save(qList);
        cache(savedQuestions);
        return savedQuestions;
    }

    /**
     * Add a collection of Question objects to the cache. If the object already exists in the cached
     * questions list, they are replaced by the ones passed in through this list
     *
     * @param qList
     */
    private void cache(List<Question> qList) {
        if (qList == null || qList.isEmpty()) {
            return;
        }

        Map<Object, Object> cacheMap = new HashMap<Object, Object>();
        for (Question qn : qList) {
            if (qn == null) {
                continue;
            }
            String cacheKey = null;
            try {
                cacheKey = getCacheKey(qn);
                cacheMap.put(cacheKey, qn);
            } catch (CacheException e) {
                log.log(Level.WARNING, e.getMessage());
            }
        }

        putObjects(cache, cacheMap);
    }

    /**
     * Remove a collection of questions from the cache
     *
     * @param qList
     */
    private void uncache(List<Question> qList) {
        if (qList == null || qList.isEmpty()) {
            return;
        }

        for (Question qn : qList) {
            if (qn == null) {
                continue;
            }
            String cacheKey;
            try {
                cacheKey = getCacheKey(qn);
                if (containsKey(cache, cacheKey)) {
                    cache.remove(cacheKey);
                }
            } catch (CacheException e) {
                log.log(Level.WARNING, e.getMessage());
            }
        }
    }

    /**
     * finds a question by its reference id
     *
     * @param refid
     * @return
     * @deprecated
     */
    @Deprecated
    public Question findByReferenceId(String refid) {
        Question q = findByProperty("referenceIndex", refid, "String");
        return q;
    }

    /**
     * finds a question by its id. If needDetails is true, all child objects (options, help,
     * translations) will also be loaded.
     *
     * @param id
     * @param needDetails
     * @return
     */
    public Question getByKey(Long id, boolean needDetails) {
        Question q = getByKey(id);
        if (needDetails) {
            q.setQuestionHelpMediaMap(helpDao.listHelpByQuestion(q.getKey()
                    .getId()));
            if (Question.Type.OPTION == q.getType()) {
                q.setQuestionOptionMap(optionDao.listOptionByQuestion(q
                        .getKey().getId()));
            }
            q.setTranslationMap(translationDao.findTranslations(
                    Translation.ParentType.QUESTION_TEXT, q.getKey().getId()));
            // only load scoring rules for types that support scoring
            if (Question.Type.OPTION == q.getType()
                    || Question.Type.FREE_TEXT == q.getType()
                    || Question.Type.NUMBER == q.getType()) {
                q.setScoringRules(scoringRuleDao.listRulesByQuestion(q.getKey()
                        .getId()));
            }
        }
        return q;
    }

    /**
     * finds the base question (no child objects) by id
     */
    @Override
    public Question getByKey(Key key) {
        return super.getByKey(key);
    }

    /**
     * Find a question based on the id in string form
     */
    @Override
    public Question getByKey(Long questionId) {
        Question question = null;
        String cacheKey = null;

        // retrieve from cache
        try {
            cacheKey = getCacheKey(questionId.toString());
            if (containsKey(cache, cacheKey)) {
                return (Question) cache.get(cacheKey);
            }
        } catch (CacheException e) {
            log.log(Level.WARNING, e.getMessage());
        }

        // else from datastore and attempt to cache
        question = super.getByKey(questionId);
        cache(Arrays.asList(question));

        return question;
    }

    /**
     * lists questions within a group ordered by creation date
     *
     * @param questionGroupId
     * @return
     */
    public List<Question> listQuestionsByQuestionGroupOrderByCreatedDateTime(
            Long questionGroupId) {
        return listByProperty("questionGroupId", questionGroupId, "Long",
                "createdDateTime", "asc");
    }

    /**
     * lists questions within a group. If needDetails flag is true, the child objects will be loaded
     * for each question. Due to processing constraints on GAE, needDetails should only be true when
     * calling this method if being called from a backend or task.
     *
     * @param questionGroupId
     * @param needDetails
     * @return
     */
    public TreeMap<Integer, Question> listQuestionsByQuestionGroup(
            Long questionGroupId, boolean needDetails) {
        return listQuestionsByQuestionGroup(questionGroupId, needDetails, true);
    }

    /**
     * lists all the questions in a group, optionally loading details. If allowSideEffects is true,
     * it will attempt to reorder any duplicated question orderings on retrieval. New users of this
     * method should ALWAY call this with allowSideEffects = false
     *
     * @param questionGroupId
     * @param needDetails
     * @param allowSideEffects
     * @return
     */
    public TreeMap<Integer, Question> listQuestionsByQuestionGroup(
            Long questionGroupId, boolean needDetails, boolean allowSideEffects) {
        List<Question> qList = listByProperty("questionGroupId",
                questionGroupId, "Long", "order", "asc");
        TreeMap<Integer, Question> map = new TreeMap<Integer, Question>();
        if (qList != null) {
            for (Question q : qList) {

                if (needDetails) {
                    q.setQuestionHelpMediaMap(helpDao.listHelpByQuestion(q
                            .getKey().getId()));
                    if (Question.Type.OPTION == q.getType()
                            || Question.Type.STRENGTH == q.getType()) {
                        q.setQuestionOptionMap(optionDao.listOptionByQuestion(q
                                .getKey().getId()));
                    }
                    q.setTranslationMap(translationDao.findTranslations(
                            ParentType.QUESTION_TEXT, q.getKey().getId()));
                    // only load scoring rules for types that support
                    // scoring
                    if (Question.Type.OPTION == q.getType()
                            || Question.Type.FREE_TEXT == q.getType()
                            || Question.Type.NUMBER == q.getType()) {
                        q.setScoringRules(scoringRuleDao.listRulesByQuestion(q
                                .getKey().getId()));
                    }
                }
                if (q.getOrder() == null) {
                    q.setOrder(qList.size() + 1);
                } else if (allowSideEffects) {
                    if (map.size() > 0 && !(q.getOrder() > map.size())) {
                        q.setOrder(map.size() + 1);
                        super.save(q);
                    } else if (map.size() == 0) {
                        super.save(q);
                    }
                }
                map.put(q.getOrder(), q);
            }
        }
        return map;
    }

    /**
     * finds q question by its path and order. Path is defined as the name of the
     * "surveyGroupName/surveyName/QuestionGroupName"
     *
     * @param order
     * @param path
     * @return
     */
    @SuppressWarnings("unchecked")
    public Question getByPath(Integer order, String path) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Question.class);
        query.setFilter(" path == pathParam && order == orderParam");
        query.declareParameters("String pathParam, String orderParam");
        List<Question> results = (List<Question>) query.execute(path, order);
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * finds a question within a group by matching on the questionText passed in
     *
     * @param questionGroupId
     * @param questionText
     * @return
     */
    @SuppressWarnings("unchecked")
    public Question getByQuestionGroupId(Long questionGroupId,
            String questionText) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Question.class);
        query.setFilter(" questionGroupId == questionGroupIdParam && text == questionTextParam");
        query.declareParameters("Long questionGroupIdParam, String questionTextParam");
        List<Question> results = (List<Question>) query.execute(
                questionGroupId, questionText);
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * finds a question by groupId and order. If there are questions with duplicated orders, the
     * first is returned.
     *
     * @param questionGroupId
     * @param order
     * @return
     */
    @SuppressWarnings("unchecked")
    public Question getByGroupIdAndOrder(Long questionGroupId, Integer order) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Question.class);
        query.setFilter(" questionGroupId == questionGroupIdParam && order == orderParam");
        query.declareParameters("Long questionGroupIdParam, Integer orderParam");
        List<Question> results = (List<Question>) query.execute(
                questionGroupId, order);
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * updates ONLY the order field within the question object for the questions passed in. All
     * questions must exist in the datastore
     *
     * @param questionList
     */
    public void updateQuestionOrder(List<Question> questionList) {
        if (questionList != null) {
            for (Question q : questionList) {
                Question persistentQuestion = getByKey(q.getKey());
                persistentQuestion.setOrder(q.getOrder());
                // since the object is still attached, we don't need to call
                // save. It will be saved on flush of the Persistent session
            }
        }
    }

    /**
     * updates ONLY the order field within the question group object for the questions passed in.
     * All question groups must exist in the datastore
     *
     * @param questionList
     */
    public void updateQuestionGroupOrder(List<QuestionGroup> groupList) {
        if (groupList != null) {
            for (QuestionGroup q : groupList) {
                QuestionGroup persistentGroup = getByKey(q.getKey(),
                        QuestionGroup.class);
                persistentGroup.setOrder(q.getOrder());
                // since the object is still attached, we don't need to call
                // save. It will be saved on flush of the Persistent session
            }
        }
    }

    /**
     * lists all questions that depend on the id passed in
     *
     * @param questionId
     * @return
     */
    public List<Question> listQuestionsByDependency(Long questionId) {
        return listByProperty("dependentQuestionId", questionId, "Long");
    }

    /**
     * Returns a list of questions whose responses will be shown as the display name of a data
     * point. The returned list of questions is ordered by question group order and then by question
     * order within a group
     *
     * @param surveyId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Question> listDisplayNameQuestionsBySurveyId(Long surveyId) {
        QuestionGroupDao qgDao = new QuestionGroupDao();
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Question.class);
        query.setFilter("surveyId == surveyIdParam && localeNameFlag == true");
        query.declareParameters("Long surveyIdParam");
        query.setOrdering("order asc");
        List<Question> results = (List<Question>) query.execute(
                surveyId);
        if (results != null && results.size() > 0) {
            SortedMap<Integer, Question> orderedQuestionMap = new TreeMap<Integer, Question>();
            for (Question question : results) {
                int orderIndex = 0;
                QuestionGroup qg = qgDao.getByKey(question.getQuestionGroupId());
                if (qg != null) {
                    orderIndex = (qg.getOrder() != null ? qg.getOrder() * 1000 : 0);
                    orderIndex += (question.getOrder() != null ? question.getOrder() : 0);
                }
                orderedQuestionMap.put(orderIndex, question);
            }
            return new ArrayList<Question>(orderedQuestionMap.values());
        } else {
            return Collections.emptyList();
        }
    }

    public List<Question> listByCascadeResourceId(Long cascadeResourceId) {
        return listByProperty("cascadeResourceId", cascadeResourceId, "Long");
    }

    /**
     * Returns a list of questions whose sourceQuestionId parameter is included in the list of ids
     * passed in as parameter
     *
     * @param sourceQuestionIds
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Question> listBySourceQuestionId(List<Long> sourceQuestionIds) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Question.class, ":p1.contains(sourceQuestionId)");
        List<Question> results = (List<Question>) query.execute(sourceQuestionIds);
        if (results == null) {
            return Collections.emptyList();
        } else {
            return results;
        }
    }
}
