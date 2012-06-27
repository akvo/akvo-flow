/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.gwt.server.survey;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupService;

import com.gallatinsystems.survey.helper.SurveyGroupHelper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyGroupServiceImpl extends RemoteServiceServlet implements
		SurveyGroupService {

	private static final long serialVersionUID = -2794945936743643615L;

	public SurveyGroupDto deleteSurveyGroup(Long surveyGroupId) {
		// TODO implement delete survey group
		return null;
	}

	public SurveyGroupDto saveSurveyGroup(SurveyGroupDto surveyGroup) {
		SurveyGroupHelper sgh = new SurveyGroupHelper();
		com.gallatinsystems.survey.domain.SurveyGroup sg = new com.gallatinsystems.survey.domain.SurveyGroup();
		sg.setCode(surveyGroup.getCode());		
		sg = sgh.saveSurveyGroup(sg);
		surveyGroup.setKeyId(sg.getKey().getId());
		return surveyGroup;
	}

}
