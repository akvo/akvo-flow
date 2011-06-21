package org.waterforpeople.mapping.dataexport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

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

public class SurveyReplicationImporter {
	
	public static void main(String[] args) {
		SurveyReplicationImporter sri = new SurveyReplicationImporter();
	}

	
	public void executeImport(String sourceBase, String serverBase) {
		SurveyGroupDAO sgDao = new SurveyGroupDAO();
		SurveyDAO sDao = new SurveyDAO();
		QuestionGroupDao qgDao = new QuestionGroupDao();
		QuestionDao qDao = new QuestionDao();

		try {
			for (SurveyGroup sg : fetchSurveyGroups(sourceBase)) {
				System.out.println("surveygroup: " + sg.getName() + ":"
						+ sg.getCode());
				sgDao.save(sg);
				for (Survey s : fetchSurveys(sg.getKey().getId(), sourceBase)) {
					System.out.println("  survey:" + s.getCode());
					sDao.save(s);
					for (QuestionGroup qg : fetchQuestionGroups(s.getKey()
							.getId(), sourceBase)) {
						System.out.println("     qg:" + qg.getCode());
						qgDao.save(qg);
						for (Question q : fetchQuestions(qg.getKey().getId(),
								sourceBase)) {
							System.out.println("       q" + q.getText());
							qDao.save(q, qg.getKey().getId());
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
		return copyAndCreateList(sgList, sgDtoList, SurveyGroup.class);
	}

	public List<Survey> fetchSurveys(Long surveyGroupId, String serverBase)
			throws Exception {
		List<SurveyDto> surveyDtoList = BulkDataServiceClient.fetchSurveys(
				surveyGroupId, serverBase);
		List<Survey> surveyList = new ArrayList<Survey>();
		return copyAndCreateList(surveyList, surveyDtoList, Survey.class);
	}

	public List<QuestionGroup> fetchQuestionGroups(Long surveyId,
			String serverBase) throws Exception {
		List<QuestionGroupDto> qgDtoList = BulkDataServiceClient
				.fetchQuestionGroups(serverBase, surveyId.toString());
		List<QuestionGroup> qgList = new ArrayList<QuestionGroup>();
		return copyAndCreateList(qgList, qgDtoList, QuestionGroup.class);
	}

	public List<Question> fetchQuestions(Long questionGroupId, String serverBase)
			throws Exception {
		List<QuestionDto> qgDtoList = BulkDataServiceClient.fetchQuestions(
				serverBase, questionGroupId);
		List<Question> qList = new ArrayList<Question>();
		List<QuestionDto> qDtoDetailList = new ArrayList<QuestionDto>();
		SurveyServiceImpl ssi = new SurveyServiceImpl();
		for (QuestionDto dto : qgDtoList) {
			QuestionDto dtoDetail = null;
			for (int i = 0; i < 3; i++) {
				try {
					dtoDetail = (QuestionDto) BulkDataServiceClient
							.loadQuestionDetails(serverBase, dto.getKeyId());
					break;
				} catch (IOException iex) {
					System.out.print("Retrying because of timeout.");
				}
			}
			Question q = ssi.marshalQuestion(dtoDetail);
			qList.add(q);
		}
		return qList;
	}

	public static <T extends BaseDomain, U extends BaseDto> List<T> copyAndCreateList(
			List<T> canonicalList, List<U> dtoList, Class<T> clazz) {
		String surveyDtoStatus = null;

		for (U dto : dtoList) {
			T canonical = null;
			try {
				canonical = clazz.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dto instanceof SurveyDto) {
				surveyDtoStatus = ((SurveyDto) dto).getStatus();
				((SurveyDto) dto).setStatus(null);
			}
			DtoMarshaller.copyToCanonical(canonical, dto);
			if (canonical instanceof Survey) {
				if (surveyDtoStatus.equals(Survey.Status.IMPORTED)) {
					((Survey) canonical).setStatus(Survey.Status.IMPORTED);
				} else if (surveyDtoStatus.equals(Survey.Status.NOT_PUBLISHED)) {
					((Survey) canonical).setStatus(Survey.Status.NOT_PUBLISHED);
				} else if (surveyDtoStatus.equals(Survey.Status.PUBLISHED)) {
					((Survey) canonical).setStatus(Survey.Status.PUBLISHED);
				} else if (surveyDtoStatus.equals(Survey.Status.VERIFIED)) {
					((Survey) canonical).setStatus(Survey.Status.VERIFIED);
				}
			}
			canonicalList.add(canonical);
		}
		return canonicalList;
	}

}
