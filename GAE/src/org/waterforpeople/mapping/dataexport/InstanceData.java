/*  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.dataexport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.InstanceDataDto;

public class InstanceData {

    // QuestionId -> Iteration -> Answer
    public final Map<Long, SortedMap<Long, String>> responseMap;
    public final SurveyInstanceDto surveyInstanceDto;
    public long maxIterationsCount;
    public String latestApprovalStatus;

    public InstanceData(InstanceDataDto instanceDataDto, Map<Long, Map<Long, String>> responseMap) {
        this(instanceDataDto.surveyInstanceData, responseMap);
        this.latestApprovalStatus = instanceDataDto.latestApprovalStatus;
    }

    public InstanceData(SurveyInstanceDto surveyInstanceDto,
            Map<Long, Map<Long, String>> responseMap) {

        // Need to normalize the response map and add empty answers for missing iterations
        // as well as make sure that the iterations are sorted
        // TODO: Drop iterations with all empty answers (?).
        Map<Long, SortedMap<Long, String>> sortedResponseMap = new HashMap<>();

        long maxIter = 0L;

        for (Entry<Long, Map<Long, String>> entry : responseMap.entrySet()) {
            Map<Long, String> iterationsMap = entry.getValue();
            SortedMap<Long, String> sortedMap = new TreeMap<>();
            Long maxIteration = Collections.max(iterationsMap.keySet());
            maxIter = Math.max(maxIter, maxIteration);
            for (long i = 0; i <= maxIteration; i++) {
                String value = iterationsMap.get(i);
                sortedMap.put(i, value == null ? "" : value);
            }
            sortedResponseMap.put(entry.getKey(), sortedMap);
        }

        this.responseMap = sortedResponseMap;
        this.surveyInstanceDto = surveyInstanceDto;
        this.maxIterationsCount = maxIter;
    }
}
