/*  Copyright (C) 2015, 2017, 2020 Stichting Akvo (Akvo Foundation)
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.InstanceDataDto;

public class InstanceData {

    // QuestionId -> Iteration -> Answer
    public final Map<Long, SortedMap<Long, String>> responseMap;
    public final SurveyInstanceDto surveyInstanceDto;
    public Set<Long> iterationsPresent;
    public String latestApprovalStatus;

    public InstanceData(InstanceDataDto instanceDataDto, Map<Long, Map<Long, String>> responseMap) {
        this(instanceDataDto.surveyInstanceData, responseMap);
        this.latestApprovalStatus = instanceDataDto.latestApprovalStatus;
    }

    public InstanceData(SurveyInstanceDto surveyInstanceDto,
            Map<Long, Map<Long, String>> responseMap) {

        // Need to normalize the response map and add empty answers for missing iterations WHY??
        // as well as make sure that the iterations are sorted
        // Drop iterations with all empty answers
        Map<Long, SortedMap<Long, String>> sortedResponseMap = new HashMap<>();

        iterationsPresent = new TreeSet<>(); //Ordered

        //Find which iterations have answers
        for (Entry<Long, Map<Long, String>> entry : responseMap.entrySet()) {
            Map<Long, String> iterationsMap = entry.getValue();
            iterationsPresent.addAll(iterationsMap.keySet());
        }
        //Sort the nonempty answers into new instance maps
        for (Entry<Long, Map<Long, String>> entry : responseMap.entrySet()) {
            Map<Long, String> iterationsMap = entry.getValue();
            SortedMap<Long, String> sortedMap = new TreeMap<>();
            for (long i : iterationsPresent) {
                String value = iterationsMap.get(i);
                //sortedMap.put(i, value == null ? "" : value);
                if (value != null) sortedMap.put(i, value);
            }
            sortedResponseMap.put(entry.getKey(), sortedMap);
        }

        this.responseMap = sortedResponseMap;
        this.surveyInstanceDto = surveyInstanceDto;
    }

    /**
     * Add responses for questions that don't yet have any.
     * Intended for RQG sheets.
     * @param additionalResponsesMap
     * @return true if they were inserted, false if any responses overlap others
     */
    public boolean addResponses(Map<Long, Map<Long, String>> additionalResponsesMap) {
        //Find which iterations have answers
        //TODO do we have to go back and plug in empties for iterations not present before?
        for (Entry<Long, Map<Long, String>> entry : additionalResponsesMap.entrySet()) {
            Map<Long, String> iterationsMap = entry.getValue();
            iterationsPresent.addAll(iterationsMap.keySet());
        }
        for (Entry<Long, Map<Long, String>> entry : additionalResponsesMap.entrySet()) {
            Map<Long, String> iterationsMap = entry.getValue();
            SortedMap<Long, String> sortedMap = new TreeMap<>();
            for (long i : iterationsPresent) {
                String value = iterationsMap.get(i);
                sortedMap.put(i, value == null ? "" : value);
            }
            //Responses for a question should only be on ONE sheet
            if (responseMap.containsKey(entry.getKey())) {
                return false;
            }
            responseMap.put(entry.getKey(), sortedMap);
        }
        return true;
    }

    @Override
    public String toString() {
        if (surveyInstanceDto != null) {
            return surveyInstanceDto.getKeyId() + " '" + surveyInstanceDto.getSurveyedLocaleIdentifier() + "'";
        } else {
            return "InstanceData iterations " + iterationsPresent.toString();
        }
    }
}
