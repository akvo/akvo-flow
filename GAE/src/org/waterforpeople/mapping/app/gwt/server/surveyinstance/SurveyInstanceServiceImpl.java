package org.waterforpeople.mapping.app.gwt.server.surveyinstance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyInstanceServiceImpl extends RemoteServiceServlet implements
		SurveyInstanceService {

	private static final long serialVersionUID = -9175237700587455358L;

	@Override
	public void deleteSurveyInstance(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<SurveyInstanceDto> listSurveyInstance(Date beginDate) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		List<SurveyInstance> siList=null;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -5);
		beginDate = c.getTime();
		if(beginDate==null)
		siList = dao
				.list("all");
		else
			siList = dao.listByDateRange(c.getTime(), null);
				
				
		ArrayList<SurveyInstanceDto> siDtoList = new ArrayList<SurveyInstanceDto>();
		for (SurveyInstance siItem : siList)
			siDtoList.add(marshalToDto(siItem));
		return siDtoList;
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
