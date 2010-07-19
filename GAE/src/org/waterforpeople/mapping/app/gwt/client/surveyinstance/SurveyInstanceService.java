package org.waterforpeople.mapping.app.gwt.client.surveyinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveyinstance")
public interface SurveyInstanceService extends RemoteService {
	ArrayList<SurveyInstanceDto> listSurveyInstance(Date beginDate);
	SurveyInstanceDto saveSurveyInstance(SurveyInstanceDto item);
	void deleteSurveyInstance(Long id);
	public List<QuestionAnswerStoreDto> listQuestionsByInstance(Long instanceId);
}
