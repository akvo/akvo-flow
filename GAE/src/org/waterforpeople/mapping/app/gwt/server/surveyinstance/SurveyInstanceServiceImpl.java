package org.waterforpeople.mapping.app.gwt.server.surveyinstance;

import java.util.ArrayList;
import java.util.Date;

import org.waterforpeople.mapping.app.gwt.client.SurveyInstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.SurveyInstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.SurveyInstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyInstanceServiceImpl extends RemoteServiceServlet implements
		SurveyInstanceService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9175237700587455358L;

	@Override
	public void deleteSurveyInstance(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<SurveyInstanceDto> listSurveyInstance(Date beginDate) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		ArrayList<SurveyInstance> siList = (ArrayList<SurveyInstance>) dao
				.list("all");
		ArrayList<SurveyInstanceDto> siDtoList = new ArrayList<SurveyInstanceDto>();
		for (SurveyInstance siItem : siList)
			siDtoList.add(marshalToDto(siItem));
		return null;
	}

	private SurveyInstanceDto marshalToDto(SurveyInstance si) {
		SurveyInstanceDto siDto = new SurveyInstanceDto();
		DtoMarshaller.copyToDto(si, siDto);
		siDto.setQuestionAnswersStore(null);
		for(QuestionAnswerStore qas:si.getQuestionAnswersStore())
		{
			QuestionAnswerStoreDto qasDto = new QuestionAnswerStoreDto();
			DtoMarshaller.copyToDto(qas, qasDto);
			siDto.addQuestionAnswerStore(qasDto);
		}
		return siDto;
	}

	@Override
	public SurveyInstanceDto saveSurveyInstance(SurveyInstanceDto item) {
		// TODO Auto-generated method stub
		return null;
	}

}
