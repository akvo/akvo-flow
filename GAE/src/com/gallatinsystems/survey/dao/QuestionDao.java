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

package com.gallatinsystems.survey.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.NotPersistent;

import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.exceptions.IllegalDeletionException;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Question.Type;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

/**
 * saves/finds question objects
 * 
 * 
 */
public class QuestionDao extends BaseDAO<Question> {

	private QuestionOptionDao optionDao;
	private QuestionHelpMediaDao helpDao;
	private TranslationDao translationDao;
	private ScoringRuleDao scoringRuleDao;

	public QuestionDao() {
		super(Question.class);
		optionDao = new QuestionOptionDao();
		helpDao = new QuestionHelpMediaDao();
		translationDao = new TranslationDao();
		scoringRuleDao = new ScoringRuleDao();
	}

	/**
	 * lists all questions filtered by type optionally filtered by surveyId as
	 * well.
	 * 
	 * @param surveyId
	 * @param type
	 * @return
	 */
	public List<Question> listQuestionByType(Long surveyId, Question.Type type) {
		if (surveyId == null) {
			return listByProperty("type", type.toString(), "String", "order",
					"asc");
		} else {
			List<Question> allQuestionsInOrder = listQuestionInOrder(surveyId);
			List<Question> typeQuestions = new ArrayList<Question>();
			if (type != null) {
				if (allQuestionsInOrder != null) {
					for (Question q : allQuestionsInOrder) {
						if (type.equals(q.getType())) {
							typeQuestions.add(q);
						}
					}
					return typeQuestions;
				}
			}
			return allQuestionsInOrder;
		}
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
		return listByProperty("surveyId", surveyId, "Long", "order", "asc");
	}

	public void delete(Question question) throws IllegalDeletionException {
		if (question != null) {
			QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
			if (qasDao.listByQuestion(question.getKey().getId()).size() == 0) {
				for (Map.Entry<Integer, QuestionOption> qoItem : optionDao
						.listOptionByQuestion(question.getKey().getId())
						.entrySet()) {
					SurveyTaskUtil.spawnDeleteTask("deleteQuestionOptions",
							qoItem.getValue().getKey().getId());
				}
				TranslationDao tDao = new TranslationDao();
				tDao.deleteTranslationsForParent(question.getKey().getId(),
						Translation.ParentType.QUESTION_TEXT);
				// TODO:Implement help media delete
				Question q = getByKey(question.getKey());
				if (q != null) {
					int order = q.getOrder();
					Long groupId = q.getQuestionGroupId();
					super.delete(q);
					// now adjust other orders
					TreeMap<Integer, Question> groupQs = listQuestionsByQuestionGroup(
							groupId, false);
					if (groupQs != null) {
						for (Question gq : groupQs.values()) {
							if (gq.getOrder() >= order) {
								gq.setOrder(gq.getOrder() - 1);
							}
						}
					}
				}
			} else {
				throw new IllegalDeletionException(
						"Cannot delete question with id "
								+ question.getKey().getId()
								+ " ("
								+ question.getText()
								+ ") because there are already survey responses stored for this question. Please delete all survey responses first.");
			}
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
	 * lists all questions for a survey and orders them by sort order. THIS
	 * METHOD SHOULD NOT BE USED AS SORT ORDERS MAY BE DUPLICATED ACROSS
	 * QUESTIONGROUPS SO THE ORDERING IS UNDEFINED
	 * 
	 * @param surveyId
	 * @return
	 * @deprecated
	 */
	public List<Question> listQuestionInOrder(Long surveyId) {
		List<Question> orderedQuestionList = new ArrayList<Question>();
		List<Question> unknownOrder = listByProperty("surveyId", surveyId,
				"Long", "order", "asc");
		QuestionGroupDao qgDao = new QuestionGroupDao();

		List<QuestionGroup> qgList = qgDao.listQuestionGroupBySurvey(surveyId);
		for (QuestionGroup qg : qgList) {
			for (Question q : unknownOrder) {
				if (qg.getKey().getId() == q.getQuestionGroupId()) {
					orderedQuestionList.add(q);
				}
			}
		}
		return orderedQuestionList;
	}

	/**
	 * list questions in order, by using question groups. Optionally filtered by type
	 * author: Mark Tiele Westra
	 * @param surveyId
	 * @return
	 */
	public List<Question> listQuestionsInOrder(Long surveyId, Question.Type type) {
		List<Question> orderedQuestionList = new ArrayList<Question>();
		QuestionGroupDao qgDao = new QuestionGroupDao();
		List<QuestionGroup> qgList = qgDao.listQuestionGroupBySurvey(surveyId);
		// for each question group, get the questions in the right order and put them in the list
		List<Question> qList;
		for (QuestionGroup qg : qgList) {
			if (type == null){
				qList = listByProperty("questionGroupId", qg
					.getKey().getId(), "Long", "order", "asc");
			} else {
				qList = getByQuestiongroupAndType(qg.getKey().getId(),type);
			}
			if (qList != null && qList.size() > 0){
				for (Question q : qList) {
					orderedQuestionList.add(q);
				}
			}
		}
		return orderedQuestionList;
	}

	/**
	 * Lists questions by questionGroupId and type
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
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not set entity fields", e);
		}

		Key key = datastore.put(question);
		q.setKey(key);
		txn.commit();
		return q;
	}

	/**
	 * saves a question, including its question options, translations, and help
	 * media (if any).
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
					save(opt.getTranslationMap().values());
				}
			}
		}
		if (question.getTranslationMap() != null) {
			for (Translation t : question.getTranslationMap().values()) {
				if (t.getParentId() == null) {
					t.setParentId(question.getKey().getId());
				}
			}
			save(question.getTranslationMap().values());
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
					save(help.getTranslationMap().values());
				}
			}
		}
		return question;
	}

	/**
	 * finds a question by its reference id
	 * 
	 * @param refid
	 * @return
	 * @deprecated
	 */
	public Question findByReferenceId(String refid) {
		Question q = findByProperty("referenceIndex", refid, "String");
		return q;
	}

	/**
	 * finds a question by its id. If needDetails is true, all child objects
	 * (options, help, translations) will also be loaded.
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
	public Question getByKey(Key key) {
		return super.getByKey(key);
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
	 * lists questions within a group. If needDetails flag is true, the child
	 * objects will be loaded for each question. Due to processing constraints
	 * on GAE, needDetails should only be true when calling this method if being
	 * called from a backend or task.
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
	 * lists all the questions in a group, optionally loading details. If
	 * allowSideEffects is true, it will attempt to reorder any duplicated
	 * question orderings on retrieval. New users of this method should ALWAY
	 * call this with allowSideEffects = false
	 * 
	 * @param questionGroupId
	 * @param needDetails
	 * @param allowSideEffects
	 * @return
	 * 
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
	 * finds q question by its path and order. Path is defined as the name of
	 * the "surveyGroupName/surveyName/QuestionGroupName"
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
	 * finds a question by groupId and order. If there are questions with
	 * duplicated orders, the first is returned.
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
	 * updates ONLY the order field within the question object for the questions
	 * passed in. All questions must exist in the datastore
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
	 * updates ONLY the order field within the question group object for the
	 * questions passed in. All question groups must exist in the datastore
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

	@SuppressWarnings("unchecked")
	public List<Question> listDisplayNameQuestionsBySurveyId(Long surveyId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Question.class);
		query.setFilter("surveyId == surveyIdParam && localeNameFlag == true");
		query.declareParameters("Long surveyIdParam");
		query.setOrdering("order asc");
		List<Question> results = (List<Question>) query.execute(
				surveyId);
		if (results != null && results.size() > 0) {
			return results;
		} else {
			return Collections.emptyList();
		}
	}
}
