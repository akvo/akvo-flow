package org.waterforpeople.mapping.app.gwt.client.si2;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SurveyInstanceService extends RemoteService {
	ArrayList<SurveyInstanceDto> listSurveyInstance(Date beginDate);
	SurveyInstanceDto saveSurveyInstance(SurveyInstanceDto item);
	void deleteSurveyInstance(Long id);
}
