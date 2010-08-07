package com.gallatinsystems.survey.dao;

import java.util.List;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.google.appengine.api.datastore.Key;

public class QuestionDao extends BaseDAO<Question> {

	private QuestionOptionDao optionDao;
	private QuestionHelpMediaDao helpDao;

	public QuestionDao() {
		super(Question.class);
		optionDao = new QuestionOptionDao();
		helpDao = new QuestionHelpMediaDao();
	}

	@SuppressWarnings("unchecked")
	public List<Question> listQuestionByType(Long surveyId, Question.Type type) {
		if (surveyId == null) {
			return listByProperty("type", type.toString(), "String");
		} else {
			PersistenceManager pm = PersistenceFilter.getManager();
			javax.jdo.Query query = pm.newQuery(Question.class);
			query.setFilter("surveyId == surveyIdParam && type == typeParam");
			query.declareParameters("Long surveyIdParam, String typeParam");
			return (List<Question>) query.execute(surveyId, type);
		}
	}

	/**
	 * loads the Question object but NOT any associated options
	 * 
	 * @param id
	 * @return
	 */
	public Question getQuestionHeader(Long id) {
		return super.getByKey(id);

	}

	public void delete(Question question, Long questionGroupId) {
		super.delete(question);

	}

	public Question save(Question question, Long questionGroupId) {
		if (questionGroupId != null) {
			question.setQuestionGroupId(questionGroupId);
			QuestionGroup group = getByKey(questionGroupId, QuestionGroup.class);
			if (group != null) {
				question.setSurveyId(group.getSurveyId());
			}
		}
		question = save(question);
		if (question.getQuestionOptionMap() != null) {
			for (QuestionOption opt : question.getQuestionOptionMap().values()) {
				opt.setQuestionId(question.getKey().getId());
				save(opt);
			}
		}
		if (question.getQuestionHelpMediaMap() != null) {
			for (QuestionHelpMedia help : question.getQuestionHelpMediaMap()
					.values()) {
				help.setQuestionId(question.getKey().getId());
				save(help);
			}
		}
		return question;
	}

	public Question findByReferenceId(String refid) {
		Question q = findByProperty("referenceIndex", refid, "String");
		return q;
	}


	public Question getByKey(Long id, boolean needDetails) {
		Question q = getByKey(id);
		if (needDetails) {
			q.setQuestionHelpMediaMap(helpDao.listHelpByQuestion(q.getKey()
					.getId()));
			if (Question.Type.OPTION == q.getType()) {
				q.setQuestionOptionMap(optionDao.listOptionByQuestion(q
						.getKey().getId()));
			}
		}
		return q;
	}

	public Question getByKey(Key key) {
		return super.getByKey(key);
	}

	public TreeMap<Integer, Question> listQuestionsByQuestionGroup(
			Long questionGroupId, boolean needDetails) {
		List<Question> qList = listByProperty("questionGroupId",
				questionGroupId, "Long");
		TreeMap<Integer, Question> map = new TreeMap<Integer, Question>();
		if (qList != null) {
			int i = 1;
			for (Question q : qList) {
				map.put(q.getOrder() != null ? q.getOrder() : i, q);
				i++;
				if (needDetails) {
					q.setQuestionHelpMediaMap(helpDao.listHelpByQuestion(q
							.getKey().getId()));
					if (Question.Type.OPTION == q.getType()) {
						q.setQuestionOptionMap(optionDao.listOptionByQuestion(q
								.getKey().getId()));
					}
				}
			}
		}
		return map;
	}

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

}
