package com.gallatinsystems.survey.dao;

import java.io.Serializable;
import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.OptionContainerQuestionOptionAssoc;

public class OptionContainerQuestionOptionAssocDao extends
		BaseDAO<OptionContainerQuestionOptionAssoc> implements Serializable {
	private static final long serialVersionUID = 7188189824027520179L;

	public OptionContainerQuestionOptionAssocDao() {
		super(OptionContainerQuestionOptionAssoc.class);
	}

	public List<OptionContainerQuestionOptionAssoc> listByOptionContainerId(
			Long optionContainerId) {
		return super.listByProperty("optionContainerId", optionContainerId,
				"Long");
	}

	public List<OptionContainerQuestionOptionAssoc> listByQuestionOptionId(
			Long questionOptionId) {
		return super.listByProperty("questionOptionId", questionOptionId,
				"Long");
	}

}
