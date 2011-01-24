package com.gallatinsystems.survey.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;

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
import com.google.appengine.api.datastore.Key;

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

	@SuppressWarnings("unchecked")
	public List<Question> listQuestionByType(Long surveyId, Question.Type type) {
		if (surveyId == null) {
			return listByProperty("type", type.toString(), "String", "order",
					"asc");
		} else {
			PersistenceManager pm = PersistenceFilter.getManager();
			javax.jdo.Query query = pm.newQuery(Question.class);
			query.setFilter("surveyId == surveyIdParam && type == typeParam");
			query.declareParameters("Long surveyIdParam, String typeParam");
			query.setOrdering("order asc");
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
		QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
		if (qasDao.listByQuestion(question.getKey().getId()).size() == 0) {
			for (Map.Entry<Integer, QuestionOption> qoItem : optionDao
					.listOptionByQuestion(question.getKey().getId()).entrySet()) {
				SurveyTaskUtil.spawnDeleteTask("deleteQuestionOptions", qoItem
						.getValue().getKey().getId());
			}
			TranslationDao tDao = new TranslationDao();
			tDao.deleteTranslationsForParent(question.getKey().getId(),
					Translation.ParentType.QUESTION_TEXT);
			// TODO:Implement help media delete
			Question q = getByKey(question.getKey());
			if (q != null) {
				super.delete(q);
			}
		} else {
			throw new IllegalDeletionException(
					"Cannot delete questionId: "
							+ question.getKey().getId()
							+ " surveyCode:"
							+ question.getText()
							+ " because there is a QuestionAnswerStore value for this question. Please delete all survey response first");
		}

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

	public Question getByKey(Key key) {
		return super.getByKey(key);
	}

	public TreeMap<Integer, Question> listQuestionsByQuestionGroup(
			Long questionGroupId, boolean needDetails) {
		List<Question> qList = listByProperty("questionGroupId",
				questionGroupId, "Long", "order", "asc");
		TreeMap<Integer, Question> map = new TreeMap<Integer, Question>();
		if (qList != null) {
			int i = 1;
			for (Question q : qList) {
				if (q.getOrder() == null)
					q.setOrder(qList.size() + 1);
				map.put(q.getOrder(), q);
				i++;
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
					// only load scoring rules for types that support scoring
					if (Question.Type.OPTION == q.getType()
							|| Question.Type.FREE_TEXT == q.getType()
							|| Question.Type.NUMBER == q.getType()) {
						q.setScoringRules(scoringRuleDao.listRulesByQuestion(q
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

	@SuppressWarnings("unchecked")
	public Question getByQuestionGroupId(Long questionGroupId,
			String questionText) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Question.class);
		query
				.setFilter(" questionGroupId == questionGroupIdParam && text == questionTextParam");
		query
				.declareParameters("Long questionGroupIdParam, String questionTextParam");
		List<Question> results = (List<Question>) query.execute(
				questionGroupId, questionText);
		if (results != null && results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Question getByGroupIdAndOrder(Long questionGroupId, Integer order) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Question.class);
		query
				.setFilter(" questionGroupId == questionGroupIdParam && order == orderParam");
		query
				.declareParameters("Long questionGroupIdParam, Integer orderParam");
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
		if(questionList != null){					
			for(Question q: questionList){
				Question persistentQuestion  = getByKey(q.getKey());
				persistentQuestion.setOrder(q.getOrder());
				//since the object is still attached, we don't need to call save. It will be saved on flush of the Persistent session 
			}			
		}
	}
}
