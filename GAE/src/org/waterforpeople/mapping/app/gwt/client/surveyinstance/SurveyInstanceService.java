package org.waterforpeople.mapping.app.gwt.client.surveyinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveyinstance")
public interface SurveyInstanceService extends RemoteService {
	 ResponseDto<ArrayList<SurveyInstanceDto>> listSurveyInstance(Date beginDate, String cursorString);
	SurveyInstanceDto saveSurveyInstance(SurveyInstanceDto item);
	public List<QuestionAnswerStoreDto> listQuestionsByInstance(Long instanceId);
}
