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

package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service to list/edit/create surveyAttributeMapping objects which are used to
 * map survey questions to attributes of another object
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("surveyattrmaprpcservice")
public interface SurveyAttributeMappingService extends RemoteService {

	public ArrayList<SurveyAttributeMappingDto> listMappingsBySurvey(
			Long surveyId);

	public TreeMap<String,String> listObjectAttributes(String objectName);
	
	public ArrayList<SurveyAttributeMappingDto> saveMappings(ArrayList<SurveyAttributeMappingDto> mappings);
	public ArrayList<SurveyAttributeMappingDto> saveMappings(Long questionGroupId, ArrayList<SurveyAttributeMappingDto> mappings);

}
