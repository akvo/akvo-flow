package com.gallatinsystems.survey.dao;

import java.util.List;
import java.util.logging.Logger;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.OptionContainer;
import com.gallatinsystems.survey.domain.OptionContainerQuestionOptionAssoc;
import com.gallatinsystems.survey.domain.QuestionOption;

public class OptionContainerDao extends BaseDAO<OptionContainer> {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(OptionContainerDao.class
			.getName());

	public OptionContainerDao() {
		super(OptionContainer.class);
	}

	public OptionContainer save(OptionContainer oc) {
		QuestionOptionDao optDao = new QuestionOptionDao();
		OptionContainerQuestionOptionAssocDao qcqoDao = new OptionContainerQuestionOptionAssocDao();
		oc = super.save(oc);
		// Get All OptionContainerQuestionOptionAssocs and delete them

		Long ocId = oc.getKey().getId();
		// Temp fix to delete assocs prior to saving to eliminate dups
		if (ocId != null)
			for (OptionContainerQuestionOptionAssoc ocqoa : qcqoDao
					.listByOptionContainerId(ocId)) {
				qcqoDao.delete(ocqoa);
			}

		if (oc.getOptionsList() != null)
			for (QuestionOption qo : oc.getOptionsList()) {
				// ToDo: change this to use unowned keys instead of assoc tables
				qo = optDao.save(qo);
				OptionContainerQuestionOptionAssoc oqqoa = new OptionContainerQuestionOptionAssoc();
				// ToDo Lookup and delete or update then insert
				oqqoa.setOptionContianerId(oc.getKey().getId());
				oqqoa.setQuestionOptionId(qo.getKey().getId());
				oqqoa = qcqoDao.save(oqqoa);
			}

		return oc;
	}

	public OptionContainer getById(Long id) {
		OptionContainer oc = super.getByKey(id);
		if (oc != null)
			setQuestionOptionList(oc);
		return oc;
	}

	public OptionContainer findByQuestionId(Long questionId) {
		OptionContainer oc = super.findByProperty("questionId", questionId,
				"Long");
		if (oc != null)
			setQuestionOptionList(oc);
		return oc;
	}

	private void setQuestionOptionList(OptionContainer oc) {
		QuestionOptionDao optDao = new QuestionOptionDao();
		OptionContainerQuestionOptionAssocDao qcqoDao = new OptionContainerQuestionOptionAssocDao();
		List<OptionContainerQuestionOptionAssoc> ocqoaList = qcqoDao
				.listByOptionContainerId(oc.getKey().getId());
		// log.info("size"+ocqoaList.size());
		for (OptionContainerQuestionOptionAssoc ocqoa : ocqoaList) {
			QuestionOption qo = optDao.getByKey(ocqoa.getQuestionOptionId());
			oc.addQuestionOption(qo);
		}
	}
}
