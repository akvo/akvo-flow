package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveyrpcservice")
public interface SurveyService extends RemoteService {

	public static final String DATE_ROLL_UP = "collectionDate";
	// TODO: change this to the region field name once it's added
	public static final String REGION_ROLL_UP = "collectionDate";

	public SurveyDto[] listSurvey();

	public SurveyActivityDto[] listSurveyActivityByDate(Date startDate,
			Date endDate, String rollUpType);

	public SurveyQuestionDto[] listSurveyQuestionByType(String typeCode);
	public ArrayList<SurveyGroupDto> listSurveyGroups(String cursorString);
	public ArrayList<SurveyDto> getSurveyGroup(String surveyGroupCode);
	/**
	 * lists all surveys for a group
	 */
	public ArrayList<SurveyDto> listSurveysByGroup(String surveyGroupCode);
}
