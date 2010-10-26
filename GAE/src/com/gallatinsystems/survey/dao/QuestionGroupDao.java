package com.gallatinsystems.survey.dao;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class QuestionGroupDao extends BaseDAO<QuestionGroup> {

	public QuestionGroupDao() {
		super(QuestionGroup.class);
	}

	public QuestionGroup save(QuestionGroup item, Long surveyId, Integer order) {
		item = save(item);
		return item;
	}

	public void delete(QuestionGroup item, Long surveyId) {

		delete(item);
	}

	public QuestionGroup getId(String questionGroupCode) {
		return findByProperty("code", questionGroupCode, "String");
	}

	public TreeMap<Integer, QuestionGroup> listQuestionGroupsBySurvey(
			Long surveyId) {
		List<QuestionGroup> groups = listByProperty("surveyId", surveyId,
				"Long");
		TreeMap<Integer, QuestionGroup> map = new TreeMap<Integer, QuestionGroup>();
		if (groups != null) {
			int i = 1;
			for (QuestionGroup group : groups) {
				map.put(group.getOrder() != null ? group.getOrder() : i, group);
				i++;
			}

		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public QuestionGroup getByPath(String code, String path) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(QuestionGroup.class);
		query.setFilter(" path == pathParam && code == codeParam");
		query.declareParameters("String pathParam, String codeParam");
		List<QuestionGroup> results = (List<QuestionGroup>) query.execute(path,
				code);
		if (results != null && results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}
	public void delete(QuestionGroup item){
		QuestionDao qDao = new QuestionDao();
		for(Map.Entry<Integer,Question> qItem: qDao.listQuestionsByQuestionGroup(item.getKey().getId(), false).entrySet()){
			SurveyTaskUtil.spawnDeleteTask("deleteQuestion",qItem.getValue().getKey().getId());
		}
		super.delete(item);
	}
	
	
}
