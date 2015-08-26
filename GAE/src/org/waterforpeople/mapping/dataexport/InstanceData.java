
package org.waterforpeople.mapping.dataexport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;

public class InstanceData {
    private SurveyInstanceDto surveyInstanceDto;
    // QuestionId -> Iteration -> Answer
    private Map<Long, SortedMap<Long, String>> responseMap;
    private Long maxIterationsCount;

    public InstanceData(SurveyInstanceDto surveyInstanceDto,
            Map<Long, Map<Long, String>> responseMap) {
        this.surveyInstanceDto = surveyInstanceDto;

        // Need to normalize the response map and add empty answers for missing iterations
        // as well as make sure that the iterations are sorted
        // TODO: Drop iterations with all empty answers.
        Map<Long, SortedMap<Long, String>> sortedResponseMap = new HashMap<>();

        Long maxIter = 0L;

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
        this.maxIterationsCount = maxIter;
    }

    public Map<Long, SortedMap<Long, String>> getResponseMap() {
        return this.responseMap;
    }

    public SurveyInstanceDto getDto() {
        return surveyInstanceDto;
    }

    public Long getMaxIterationsCount() {
        return maxIterationsCount;
    }

}
