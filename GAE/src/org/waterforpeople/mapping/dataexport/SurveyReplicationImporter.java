package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.framework.dataexport.applet.DataImporter;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;

public class SurveyReplicationImporter implements DataImporter {
	
	@Override
	public Map<Integer, String> validate(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeImport(File file, String serverBase) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SurveyReplicationImporter sri = new SurveyReplicationImporter();
		sri.executeImport("localhost:8888", "watermapmonitordev.appspot.com");

	}

	@Override
	public void executeImport(String sourceBase, String serverBase) {
		SurveyGroupDAO sgDao = new SurveyGroupDAO();
		SurveyDAO sDao = new SurveyDAO();
		QuestionGroupDao qgDao = new QuestionGroupDao();
		QuestionDao qDao = new QuestionDao();
		
		try {
			for(SurveyGroup sg:fetchSurveyGroups(sourceBase)){
				sgDao.save(sg);
				for(Survey s:fetchSurveys(sg.getKey().getId(),sourceBase)){
					sDao.save(s);
					for(QuestionGroup qg:fetchQuestionGroups(s.getKey().getId(),sourceBase)){
						qgDao.save(qg);
						for(Question q:fetchQuestions(qg.getKey().getId(),sourceBase)){
							qDao.save(q);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<SurveyGroup> fetchSurveyGroups(String serverBase)
			throws Exception {
		List<SurveyGroupDto> sgDtoList = BulkDataServiceClient
				.fetchSurveyGroups(serverBase);
		List<SurveyGroup> sgList = new ArrayList<SurveyGroup>();
		return copyAndCreateList(sgList, sgDtoList, new SurveyGroup());
	}

	public List<Survey> fetchSurveys(Long surveyGroupId, String serverBase)
			throws Exception {
		List<SurveyDto> surveyDtoList = BulkDataServiceClient.fetchSurveys(
				surveyGroupId, serverBase);
		List<Survey> surveyList = new ArrayList<Survey>();
		return copyAndCreateList(surveyList, surveyDtoList, new Survey());
	}

	public List<QuestionGroup> fetchQuestionGroups(Long surveyId,
			String serverBase) throws Exception {
		List<QuestionGroupDto> qgDtoList = BulkDataServiceClient
				.fetchQuestionGroups(serverBase, surveyId.toString());
		List<QuestionGroup> qgList = new ArrayList<QuestionGroup>();
		return copyAndCreateList(qgList, qgDtoList, new QuestionGroup());
	}

	public List<Question> fetchQuestions(Long questionGroupId, String serverBase)
			throws Exception {
		List<QuestionDto> qgDtoList = BulkDataServiceClient.fetchQuestions(
				serverBase, questionGroupId);
		List<Question> qgList = new ArrayList<Question>();
		return copyAndCreateList(qgList, qgDtoList, new Question());
	}

	public static <T extends BaseDomain, U extends BaseDto> List<T> copyAndCreateList(
			List<T> canonicalList, List<U> dtoList, T canonical) {
		for (U dto : dtoList) {
			DtoMarshaller.copyToCanonical(canonical, dto);
			canonicalList.add(canonical);
		}
		return canonicalList;
	}

}
