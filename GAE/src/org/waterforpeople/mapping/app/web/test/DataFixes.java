package org.waterforpeople.mapping.app.web.test;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

public class DataFixes {
	private static Logger log = Logger.getLogger(DataFixes.class.getName());

	@SuppressWarnings("deprecation")
	public void fixQuestionAnswerStoreCollectionDate(HttpServletRequest req,
			HttpServletResponse resp) {

		QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
		SurveyInstanceDAO siDao = new SurveyInstanceDAO();
		List<QuestionAnswerStore> qasList = qasDao
				.listByNotNullCollectionDateBefore(new Date("1/1/1900"), "all",
						500);
		for (QuestionAnswerStore item : qasList) {
			SurveyInstance si = siDao.getByKey(item.getSurveyInstanceId());
			if (si != null && si.getCollectionDate() != null) {
				item.setCollectionDate(si.getCollectionDate());
				qasDao.save(item);
				log.log(Level.INFO, "fixed: " + item.getKey()
						+ " set collectionDate to: " + item.getCollectionDate()
						+ " from surveyInstanceId: " + si.getKey().getId());
			} else {
				item.setCollectionDate(new Date("1/1/1980"));
				qasDao.save(item);
				log.log(Level.INFO,
						"fixed: "
								+ item.getKey()
								+ " set collectionDate to: "
								+ item.getCollectionDate()
								+ " Because I couldn't find a surveyinstance or it was missing a collectionDate");
			}
		}

	}

	public void generateTestData() {
		for (int i = 0; i < 1000; i++) {
			SurveyInstanceDAO siDao = new SurveyInstanceDAO();
			SurveyInstance si = new SurveyInstance();
			si.setCollectionDate(new Date());
			si.setCommunity("test");
			siDao.save(si);

			QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
			QuestionAnswerStore qas = new QuestionAnswerStore();
			qas.setCollectionDate(new Date("1/1/1000"));
			qas.setSurveyInstanceId(si.getKey().getId());
			qasDao.save(qas);
		}
	}

}
