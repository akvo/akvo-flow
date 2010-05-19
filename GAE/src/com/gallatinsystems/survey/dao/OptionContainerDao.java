package com.gallatinsystems.survey.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.OptionContainer;
import com.gallatinsystems.survey.domain.QuestionOption;


public class OptionContainerDao extends BaseDAO<OptionContainer> {

	public OptionContainerDao(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	
	public OptionContainerDao(){
		super(OptionContainer.class);
	}
	
	public OptionContainer save(OptionContainer oc){
		if(oc.getOptionsList()!=null)
			for(QuestionOption qo:oc.getOptionsList())
				new QuestionOptionDao().save(qo);
		return super.save(oc);
	}

}
