package com.gallatinsystems.survey.dao;

import java.util.logging.Logger;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.OptionContainer;
import com.gallatinsystems.survey.domain.QuestionOption;

public class OptionContainerDao extends BaseDAO<OptionContainer> {
	private static final Logger log = Logger.getLogger(OptionContainerDao.class
			.getName());

	public OptionContainerDao(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public OptionContainerDao() {
		super(OptionContainer.class);
	}

	public OptionContainer save(OptionContainer oc) {
		QuestionOptionDao optDao = new QuestionOptionDao();
		if (oc.getOptionsList() != null)
			for (QuestionOption qo : oc.getOptionsList()) {
//				qo = optDao.save(qo);
				if (qo.getKey() == null)
					log
							.info("NNNNNNNNNNOOOOOOOOOOOOOOOOKKKKKKKKKKKEEEEEEEEEEEYYYYYYY"
									+ qo.getCode());
			}
		return super.save(oc);
	}

}
