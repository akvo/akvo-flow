package com.gallatinsystems.survey.dao;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.OptionContainer;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.QuestionQuestionGroupAssoc;

public class QuestionDao extends BaseDAO<Question> {

	public QuestionDao(Class<Question> e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public QuestionDao() {
		super(Question.class);
	}

	public List<Question> listQuestionsByQuestionGroup(String questionGroupCode) {
		List<QuestionQuestionGroupAssoc> qqgaList = new QuestionQuestionGroupAssocDao()
				.listByQuestionGroupId(new Long(questionGroupCode));
		java.util.ArrayList<Question> questionList = new ArrayList<Question>();
		for (QuestionQuestionGroupAssoc qqga : qqgaList) {
			Question question = getByKey(qqga.getQuestionId());
			questionList.add(question);
//			if(question.getOptionContainer()!=null){
//				log.info("OC: " + question.getOptionContainer().getKey());
//				if(question.getOptionContainer().getOptionsList()!=null){
//					for(QuestionOption qo : question.getOptionContainer().getOptionsList()){
//						log.info("QO: " + qo.getKey() + ":" + qo.getCode()+":"+qo.getText());
//					}
//				}
//			}
//			
		}
		return questionList;
	}

	public void delete(Question question, Long questionGroupId) {
		QuestionQuestionGroupAssocDao qqgaDao = new QuestionQuestionGroupAssocDao();
		List<QuestionQuestionGroupAssoc> qqga = qqgaDao
				.listByQuestionId(question.getKey().getId());
		for (QuestionQuestionGroupAssoc item : qqga) {
			if (item.getQuestionGroupId().equals(questionGroupId))
				qqgaDao.delete(item);
		}
		question = super.getByKey(question.getKey().getId());
		super.delete(question);

	}

	public Question save(Question question, Long questionGroupId) {
		question = super.save(question);
		QuestionQuestionGroupAssoc qqga = new QuestionQuestionGroupAssoc();
		qqga.setQuestionGroupId(questionGroupId);
		qqga.setQuestionId(question.getKey().getId());
		QuestionQuestionGroupAssocDao qqgaDao = new QuestionQuestionGroupAssocDao();
		qqgaDao.save(qqga);
//		OptionContainerDao ocDao = new OptionContainerDao();
//		
//		if (question.getOptionContainer() != null){
//			OptionContainer oc = ocDao.save(question.getOptionContainer());
//		}

		return question;
	}
	public Question getByKey(Long id){
		Question q = super.getByKey(id);
		
		return q;
	}

}
