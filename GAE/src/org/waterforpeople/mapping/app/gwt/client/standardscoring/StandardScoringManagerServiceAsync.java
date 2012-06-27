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
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StandardScoringManagerServiceAsync {

	void delete(Long id, AsyncCallback<Void> callback);

	void listStandardScoring(Long scoreBucketKey, String cursorString,
			AsyncCallback<ResponseDto<ArrayList<StandardScoringDto>>> callback);

	void save(StandardScoringDto item,
			AsyncCallback<StandardScoringDto> callback);

	void listStandardScoreBuckets(
			AsyncCallback<ArrayList<StandardScoreBucketDto>> asyncCallback);

	void listObjectAttributes(String objectName,
			AsyncCallback<TreeMap<String, String>> callback);

	void save(StandardScoreBucketDto item,
			AsyncCallback<StandardScoreBucketDto> callback);

	void listStandardContainer(String standardType,
			AsyncCallback<ArrayList<StandardContainerDto>> callback);

	void saveCompoundRule(Long compoundRuleId, String standardType, String name,
			Long leftRuleId, String leftRuleType, Long rightRuleId, String rightRuleType, String operator,
			AsyncCallback<Long> callback);

	void listCompoundRule(String standardType,
			AsyncCallback<ResponseDto<ArrayList<CompoundStandardDto>>> callback);

	void deleteCompoundStandard(Long id, AsyncCallback<Void> callback);

}
