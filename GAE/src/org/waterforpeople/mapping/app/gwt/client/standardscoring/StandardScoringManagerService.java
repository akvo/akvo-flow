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

package org.waterforpeople.mapping.app.gwt.client.standardscoring;

import java.util.ArrayList;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("standardscoring")
public interface StandardScoringManagerService extends RemoteService {
	ResponseDto<ArrayList<StandardScoringDto>> listStandardScoring(
			Long scoreBucketKey, String cursorString);
	StandardScoringDto save(StandardScoringDto item);
	void delete(Long id);
	ArrayList<StandardScoreBucketDto> listStandardScoreBuckets();
	TreeMap<String, String> listObjectAttributes(String objectName);
	StandardScoreBucketDto save(StandardScoreBucketDto item);
	ArrayList<StandardContainerDto> listStandardContainer(String standardType);
	Long saveCompoundRule(Long compoundRuleId,String standardType,String name, Long leftRuleId, String leftRuleType, Long rightRuleId, String rightRuleType, String operator);
	ResponseDto<ArrayList<CompoundStandardDto>> listCompoundRule(String standardType);
	void deleteCompoundStandard(Long id);
}
