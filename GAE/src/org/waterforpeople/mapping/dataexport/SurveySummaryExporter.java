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

package org.waterforpeople.mapping.dataexport;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.framework.dataexport.applet.AbstractDataExporter;

/**
 * This exporter will write the survey "descriptive statistics" report to a file. These stats
 * include a breakdown of question response frequencies for each question in a survey.
 *
 * @author Christopher Fagiani
 */
public class SurveySummaryExporter extends AbstractDataExporter {

    private static final Logger log = Logger.getLogger(SurveySummaryExporter.class);

    public static final String RESPONSE_KEY = "dtoList";
    private static final String SERVLET_URL = "/surveyrestapi";
    private static final NumberFormat PCT_FMT = new DecimalFormat("0.00");
    protected static final String[] ROLLUP_QUESTIONS = {
            "Sector/Cell",
            "Department", "Province", "Municipality", "Region", "District",
            "Traditional Authority (TA)", "Sub-Traditional Authority (Sub-TA)",
            "County", "Sub County", "Sector", "Cell", "Gran Panchayet",
            "Block", "State", "Micro-Region"
    };
    protected static final Map<String, String[]> ROLLUP_MAP;
    static {
        ROLLUP_MAP = new HashMap<String, String[]>();
        ROLLUP_MAP.put("IN", new String[] {
                "State", "District", "Block",
                "Gran Panchayet"
        });
        ROLLUP_MAP.put("HN", new String[] {
                "Department", "Municipality",
                "Sector"
        });
        ROLLUP_MAP.put("GT", new String[] {
                "Department", "Municipality"
        });
        ROLLUP_MAP.put("DR", new String[] {
                "Province", "Municipality"
        });
        ROLLUP_MAP.put("NI", new String[] {
                "Department", "Municipality",
                "Micro-Region"
        });
        ROLLUP_MAP.put("BO", new String[] {
                "Department", "Municipality",
                "District"
        });
        ROLLUP_MAP.put("PE", new String[] {
                "Region", "Province", "District"
        });
        ROLLUP_MAP.put("EC", new String[] {
                "Province", "Municipality"
        });
        ROLLUP_MAP.put("MW", new String[] {
                "District",
                "Tradtional Authoriaty (TA)",
                "Sub-Traditional Authority (Sub-TA)"
        });
        ROLLUP_MAP.put("RW", new String[] {
                "District", "Sector", "Cell"
        });
        ROLLUP_MAP.put("UG",
                new String[] {
                        "District", "County", "Sub County"
                });
    }
    protected List<QuestionGroupDto> orderedGroupList;

    /**
     * the ordered list of "rollup questions" (i.e. so we know how to build the drill-downs)
     */
    protected List<QuestionDto> rollupOrder;

    @Override
    public void export(Map<String, String> criteria, File fileName,
            String serverBase, Map<String, String> options) {
        InputDialog dia = new InputDialog();
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(fileName);
            writeHeader(pw, dia.getDoRollup());
            Map<QuestionGroupDto, List<QuestionDto>> questionMap = loadAllQuestions(
                    criteria.get(SurveyRestRequest.SURVEY_ID_PARAM), true,
                    serverBase, criteria.get("apiKey"));
            if (questionMap.size() > 0) {
                SummaryModel model = buildDataModel(
                        criteria.get(SurveyRestRequest.SURVEY_ID_PARAM),
                        serverBase, criteria.get("apiKey"));
                for (QuestionGroupDto group : orderedGroupList) {
                    for (QuestionDto question : questionMap.get(group)) {
                        pw.print(model.outputQuestion(group.getDisplayName()
                                .trim(), question.getText().trim(), question
                                .getKeyId().toString(), dia.getDoRollup()));
                    }
                }
            } else {
                log.info("No questions for survey: "
                        + criteria.get(SurveyRestRequest.SURVEY_ID_PARAM) + " - instance: "
                        + serverBase);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    protected SummaryModel buildDataModel(String surveyId, String serverBase, String apiKey)
            throws Exception {
        SummaryModel model = new SummaryModel();
        Map<String, String> instanceMap = BulkDataServiceClient
                .fetchInstanceIds(surveyId, serverBase, apiKey, false);
        for (String instanceId : instanceMap.keySet()) {
            Map<String, String> responseMap = BulkDataServiceClient
                    .fetchQuestionResponses(instanceId, serverBase, apiKey);
            Set<String> rollups = null;
            if (rollupOrder != null && rollupOrder.size() > 0) {
                rollups = formRollupStrings(responseMap);
            }
            for (Entry<String, String> entry : responseMap.entrySet()) {
                model.tallyResponse(entry.getKey(), rollups, entry.getValue());
            }
        }
        return model;
    }

    /**
     * builds the keys to use for roll-ups. So if the rollupOrder contains 2 questions, say State
     * and District, it will form strings that look like: "<StateResponse>" and
     * "<StateResponse>|<DistrictResponse>"
     *
     * @param responseMap
     * @return
     */
    protected Set<String> formRollupStrings(Map<String, String> responseMap) {
        Set<String> rollups = new HashSet<String>();
        for (int j = 0; j < rollupOrder.size(); j++) {
            String rollup = "";
            int count = 0;
            for (int i = 0; i < rollupOrder.size() - j; i++) {
                String val = responseMap.get(rollupOrder.get(i).getKeyId()
                        .toString());
                if (val != null && val.trim().length() > 0) {
                    if (count > 0) {
                        rollup += "|";
                    }
                    rollup += val;
                    count++;
                }
            }
            rollups.add(rollup);
        }
        return rollups;

    }

    protected List<SurveyGroupDto> fetchSurveyGroup(String surveyId, String serverBase,
            String apiKey) throws Exception {

        return parseSurveyGroups(BulkDataServiceClient.fetchDataFromServer(
                serverBase + SERVLET_URL, "action="
                        + SurveyRestRequest.GET_SURVEY_GROUP_ACTION + "&"
                        + SurveyRestRequest.SURVEY_ID_PARAM + "="
                        + surveyId, true, apiKey));
    }

    protected Map<QuestionGroupDto, List<QuestionDto>> loadAllQuestions(
            String surveyId, boolean performRollups, String serverBase, String apiKey)
            throws Exception {
        Map<QuestionGroupDto, List<QuestionDto>> questionMap = new HashMap<QuestionGroupDto, List<QuestionDto>>();
        orderedGroupList = fetchQuestionGroups(serverBase, surveyId, apiKey);
        rollupOrder = new ArrayList<QuestionDto>();
        for (QuestionGroupDto group : orderedGroupList) {
            List<QuestionDto> questions = fetchQuestions(serverBase,
                    group.getKeyId(), apiKey);
            if (performRollups && questions != null) {
                for (QuestionDto q : questions) {

                    for (int i = 0; i < ROLLUP_QUESTIONS.length; i++) {
                        if (ROLLUP_QUESTIONS[i].equalsIgnoreCase(q.getText())) {
                            rollupOrder.add(q);
                        }
                    }
                }
            }
            questionMap.put(group, questions);
        }
        return questionMap;
    }

    private void writeHeader(PrintWriter pw, boolean isRolledUp) {
        if (isRolledUp) {
            pw.println("Question Group\tQuestion\tSector\tResponse\tFrequency\tPercent\tMean\tMedian\tMode\tStd Dev\tStd Err\tRange");
        } else {
            pw.println("Question Group\tQuestion\tResponse\tFrequency\tPercent\tMean\tMedian\tMode\tStd Dev\tStd Err\tRange");
        }
    }

    protected List<QuestionDto> fetchQuestions(String serverBase, Long groupId, String apiKey)
            throws Exception {

        return parseQuestions(BulkDataServiceClient.fetchDataFromServer(
                serverBase + SERVLET_URL, "action="
                        + SurveyRestRequest.LIST_QUESTION_ACTION + "&"
                        + SurveyRestRequest.QUESTION_GROUP_ID_PARAM + "="
                        + groupId, true, apiKey));
    }

    protected List<QuestionGroupDto> fetchQuestionGroups(String serverBase,
            String surveyId, String apiKey) throws Exception {
        return parseQuestionGroups(BulkDataServiceClient.fetchDataFromServer(
                serverBase + SERVLET_URL, "action="
                        + SurveyRestRequest.LIST_GROUP_ACTION + "&"
                        + SurveyRestRequest.SURVEY_ID_PARAM + "=" + surveyId,
                true, apiKey));
    }

    protected List<QuestionGroupDto> parseQuestionGroups(String response)
            throws Exception {
        List<QuestionGroupDto> dtoList = new ArrayList<QuestionGroupDto>();
        JSONArray arr = getJsonArray(response);
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);
                if (json != null) {
                    QuestionGroupDto dto = new QuestionGroupDto();
                    try {
                        if (json.has("code")) {
                            dto.setCode(json.getString("code"));
                        }
                        if (json.has("keyId")) {
                            dto.setKeyId(json.getLong("keyId"));
                        }
                        dtoList.add(dto);
                    } catch (Exception e) {
                        log.error("Error in json parsing: " + e.getMessage(), e);
                    }
                }
            }
        }
        return dtoList;
    }

    protected List<QuestionDto> parseQuestions(String response)
            throws Exception {
        List<QuestionDto> dtoList = new ArrayList<QuestionDto>();
        JSONArray arr = getJsonArray(response);
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);
                if (json != null) {
                    QuestionDto dto = new QuestionDto();
                    try {
                        if (json.has("text")) {
                            dto.setText(json.getString("text"));
                        }
                        if (json.has("keyId")) {
                            dto.setKeyId(json.getLong("keyId"));
                        }
                        if (json.has("questionTypeString")) {
                            dto.setType(QuestionType.valueOf(json
                                    .getString("questionTypeString")));
                        }
                        if (!json.isNull("questionId")
                                && !"null".equals(json.getString("questionId"))) {
                            dto.setQuestionId(json.getString("questionId"));
                        }
                        if (!json.isNull("localeNameFlag")) {
                            dto.setLocaleNameFlag(json.getBoolean("localeNameFlag"));
                        }
                        if (!json.isNull("levelNames")) {
                            final List<String> levelNames = new ArrayList<String>();
                            final JSONArray array = json.getJSONArray("levelNames");
                            for (int c = 0; c < array.length(); c++) {
                                levelNames.add(array.getString(c));
                            }
                            dto.setLevelNames(levelNames);
                        }
                        dtoList.add(dto);
                    } catch (Exception e) {
                        log.error("Error in json parsing: " + e.getMessage(), e);
                    }
                }
            }
        }
        return dtoList;
    }

    protected List<SurveyGroupDto> parseSurveyGroups(String response) throws Exception {
        List<SurveyGroupDto> dtoList = new ArrayList<SurveyGroupDto>();
        JSONArray jsonArray = getJsonArray(response);

        if (jsonArray == null) {
            return dtoList;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            if (o != null) {
                try {

                    SurveyGroupDto dto = new SurveyGroupDto();

                    if (!o.isNull("name")) {
                        dto.setName(o.getString("name"));
                    }

                    if (o.isNull("monitoringGroup")) {
                        dto.setMonitoringGroup(false);
                    } else {
                        dto.setMonitoringGroup(o.getBoolean("monitoringGroup"));
                    }

                    dtoList.add(dto);
                } catch (Exception e) {
                    log.error("Error in json parsing: " + e.getMessage(), e);
                }
            }
        }

        return dtoList;
    }

    /**
     * converts the string into a JSON array object.
     */
    protected JSONArray getJsonArray(String response) throws Exception {
        log.debug("response: " + response);
        if (response != null) {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                return json.getJSONArray(RESPONSE_KEY);
            }
        }
        return null;
    }

    protected class InputDialog extends JDialog implements ActionListener {

        private static final long serialVersionUID = -2875321125734363515L;

        private JButton yesButton;
        private JButton noButton;
        private JLabel label;
        private boolean doRollup;

        public InputDialog() {
            super();
            yesButton = new JButton("Yes");
            noButton = new JButton("No");
            label = new JLabel("Roll-up by Sector/Cell?");

            JPanel contentPane = new JPanel();
            contentPane.add(label);
            JPanel buttonPane = new JPanel(new GridLayout(1, 2));
            buttonPane.add(yesButton);
            buttonPane.add(noButton);
            contentPane.add(buttonPane);
            yesButton.addActionListener(this);
            noButton.addActionListener(this);
            setContentPane(contentPane);
            setSize(300, 200);
            setTitle("Select Export Options");
            setModal(true);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == noButton) {
                doRollup = false;
            } else {
                doRollup = true;
            }
            setVisible(false);
        }

        public boolean getDoRollup() {
            return doRollup;
        }
    }

    protected class SummaryModel {
        // contains the map of questionIds to all valid responses
        private Map<String, List<String>> responseMap;
        // list of all sectors encountered
        private List<String> rollupList;
        // map of frequency counts of a response. the key is the packed value of
        // questionId+sector+response
        private Map<String, Long> sectorCountMap;
        // map of totals for all responses in a quesitonId+sector
        private Map<String, Long> sectorTotalMap;
        private Map<String, Long> responseCountMap;
        private Map<String, Long> responseTotalMap;
        // map of question to stats value
        private Map<String, DescriptiveStats> statMap;
        private Map<String, Map<String, DescriptiveStats>> sectorStatMap;

        public SummaryModel() {
            responseMap = new HashMap<String, List<String>>();
            rollupList = new ArrayList<String>();
            sectorCountMap = new HashMap<String, Long>();
            sectorTotalMap = new HashMap<String, Long>();
            responseCountMap = new HashMap<String, Long>();
            responseTotalMap = new HashMap<String, Long>();
            statMap = new HashMap<String, DescriptiveStats>();
            sectorStatMap = new HashMap<String, Map<String, DescriptiveStats>>();
        }

        public void tallyResponse(String questionId, Set<String> rollups,
                String response) {
            addResponse(questionId, response);
            addRollup(rollups);
            incrementCount(questionId, rollups, response);
            updateStats(questionId, rollups, response);
        }

        private void updateStats(String questionId, Set<String> rollups,
                String response) {
            if (statMap.get(questionId) == null) {
                DescriptiveStats stats = new DescriptiveStats();
                stats.addSample(response);
                statMap.put(questionId, stats);
            } else {
                statMap.get(questionId).addSample(response);
            }
            if (rollups != null) {
                for (String sector : rollups) {
                    if (sector != null) {
                        if (sectorStatMap.get(sector) == null) {
                            Map<String, DescriptiveStats> secStats = new HashMap<String, DescriptiveStats>();
                            sectorStatMap.put(sector, secStats);
                            DescriptiveStats stats = new DescriptiveStats();
                            stats.addSample(response);
                            secStats.put(questionId, stats);
                        } else {
                            if (sectorStatMap.get(sector).get(questionId) == null) {
                                DescriptiveStats stats = new DescriptiveStats();
                                stats.addSample(response);
                                sectorStatMap.get(sector)
                                        .put(questionId, stats);
                            } else {
                                sectorStatMap.get(sector).get(questionId)
                                        .addSample(response);
                            }
                        }
                    }
                }
            }
        }

        private void incrementCount(String questionId, Set<String> rollups,
                String response) {
            if (rollups != null) {
                for (String sector : rollups) {
                    incrementValue(questionId + sector + response,
                            sectorCountMap);
                    incrementValue(questionId + sector, sectorTotalMap);
                }
            }
            incrementValue(questionId + response, responseCountMap);
            incrementValue(questionId, responseTotalMap);
        }

        private void incrementValue(String key, Map<String, Long> map) {
            Long val = map.get(key);
            if (val == null) {
                val = 1L;
            } else {
                val++;
            }
            map.put(key, val);
        }

        private void addRollup(Set<String> rollups) {
            if (rollups != null) {
                for (String sector : rollups) {
                    if (sector != null && sector.trim().length() > 0
                            && !rollupList.contains(sector.trim())) {
                        rollupList.add(sector.trim());

                    }
                }
            }
        }

        private void addResponse(String questionId, String response) {
            List<String> responses = responseMap.get(questionId);
            if (responses == null) {
                responses = new ArrayList<String>();
                responseMap.put(questionId, responses);
            }
            if (!responses.contains(response)) {
                responses.add(response);
            }
        }

        public String outputQuestion(String groupName, String questionText,
                String questionId, boolean isRolledUp) {
            String result = null;

            if (isRolledUp) {
                StringBuilder buffer = new StringBuilder();
                for (String sector : rollupList) {
                    buffer.append(outputResponses(groupName, questionText,
                            sector, questionId, isRolledUp));
                }
                result = buffer.toString();
            } else {
                result = outputResponses(groupName, questionText, "",
                        questionId, isRolledUp);
            }
            return result;
        }

        private String outputResponses(String groupName, String questionText,
                String sector, String questionId, boolean isRolledUp) {
            StringBuilder buffer = new StringBuilder();
            if (responseMap.get(questionId) != null) {
                for (String response : responseMap.get(questionId)) {
                    Long count = null;
                    if (isRolledUp) {
                        count = sectorCountMap.get(questionId + sector
                                + response);
                    } else {
                        count = responseCountMap.get(questionId + response);
                    }
                    String countString = "0";
                    String pctString = "0";
                    if (count != null) {
                        Long total = null;
                        if (isRolledUp) {
                            total = sectorTotalMap.get(questionId + sector);
                        } else {
                            total = responseTotalMap.get(questionId);
                        }
                        pctString = PCT_FMT.format((double) count
                                / (double) total);
                        countString = count.toString();
                    }
                    buffer.append(groupName).append("\t").append(questionText)
                            .append("\t");
                    if (isRolledUp) {
                        buffer.append(sector).append("\t");
                    }
                    buffer.append(response).append("\t").append(countString)
                            .append("\t").append(pctString).append("\t")
                            .append(statMap.get(questionId).getStatsString())
                            .append("\n");
                }
            }
            return buffer.toString();
        }

        public Map<String, Long> getResponseCountsForQuestion(Long questionId,
                String sector) {
            List<String> responses = responseMap.get(questionId.toString());
            Map<String, Long> countMap = new HashMap<String, Long>();
            if (responses != null) {
                for (String resp : responses) {
                    Long count = null;
                    if (sector == null) {
                        count = responseCountMap.get(questionId + resp);
                    } else {
                        count = sectorCountMap.get(questionId + sector + resp);
                    }
                    countMap.put(resp, count != null ? count : new Long(0));
                }
            }
            return countMap;
        }

        public DescriptiveStats getDescriptiveStatsForQuestion(Long questionId,
                String sector) {
            if (sector == null) {
                return statMap.get(questionId.toString());
            } else {
                if (sectorStatMap.get(sector) != null) {
                    return sectorStatMap.get(sector).get(questionId.toString());
                } else {
                    return null;
                }
            }
        }

        public List<String> getSectorList() {
            return rollupList;
        }

    }

    protected class DescriptiveStats {
        private double max;
        private double min;
        private double mean;
        private double sumSqMean;
        private int sampleCount;
        private List<Double> valueList;
        private boolean isSorted;

        public DescriptiveStats() {
            mean = 0d;
            sampleCount = 0;
            sumSqMean = 0d;
            isSorted = false;
            valueList = new ArrayList<Double>();
            max = Double.MIN_VALUE;
            min = Double.MAX_VALUE;
        }

        public int getSampleCount() {
            return sampleCount;
        }

        public void addSample(String stringVal) {
            double val = Double.MIN_VALUE;
            try {
                val = Double.parseDouble(stringVal);
            } catch (Exception e) {
                return;
            }
            sampleCount++;
            if (val > max) {
                max = val;
            }
            if (val < min) {
                min = val;
            }
            double delta = val - mean;
            mean = mean + (delta / sampleCount);
            // the sumSqMean calc uses the newly updated value for mean
            sumSqMean = sumSqMean + delta * (val - mean);
            isSorted = false;
            valueList.add(val);
        }

        public double getMean() {
            return mean;
        }

        public double getRange() {
            return max - min;
        }

        public double getVariance() {
            return sumSqMean / (sampleCount - 1d);
        }

        public double getMedian() {
            if (!isSorted) {
                Collections.sort(valueList);
                isSorted = true;
            }
            if (valueList.size() % 2 == 1) {
                return valueList.get((int) Math.floor(valueList.size() / 2));
            } else {
                Double lowerVal = valueList.get(valueList.size() / 2);
                Double upperVal = valueList.get(valueList.size() / 2 - 1);
                return (lowerVal + upperVal) / 2;
            }
        }

        public double getMode() {
            if (!isSorted) {
                Collections.sort(valueList);
                isSorted = true;
            }
            int maxOccur = 0;
            int curOccur = 0;
            Double maxOccurValue = null;
            Double lastValue = null;
            for (Double val : valueList) {
                if (lastValue == null || !val.equals(lastValue)) {
                    if (curOccur > maxOccur) {
                        maxOccur = curOccur;
                        maxOccurValue = lastValue;
                    }
                    lastValue = val;
                    curOccur = 1;
                } else if (val.equals(lastValue)) {
                    curOccur++;
                }
            }
            if (maxOccurValue == null) {
                maxOccurValue = lastValue;
            }
            return maxOccurValue;
        }

        public double getStandardDeviation() {
            return Math.sqrt(getVariance());
        }

        public double getStandardError() {
            return Math.sqrt(getVariance() / sampleCount);
        }

        /**
         * outputs stats in the following order (tab delimited): Mean, Median, Mode, Std Dev, Std
         * Err, Range
         *
         * @return
         */
        public String getStatsString() {
            StringBuilder builder = new StringBuilder();
            if (sampleCount > 0) {
                builder.append(getMean()).append("\t").append(getMedian())
                        .append("\t").append(getMode()).append("\t")
                        .append(getStandardDeviation()).append("\t")
                        .append(getStandardError()).append("\t")
                        .append(getRange());
            } else {
                builder.append("\t\t\t\t\t");
            }
            return builder.toString();
        }

        public double getMax() {
            return max;
        }

        public double getMin() {
            return min;
        }

    }
}
