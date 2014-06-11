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

package org.waterforpeople.mapping.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizationHandler;

/**
 * Task servlet to handle bulk updates of survey data.
 * 
 * @author Christopher Fagiani
 */
public class SurveyDataUpdateHandler extends DataSummarizationHandler {
    private static final long serialVersionUID = 3873215496111195139L;

    /**
     * installs the list of summarizers that will run for various data types
     */
    @Override
    protected void initializeSummarization() {
        queueName = "dataUpdate";
        summarizerPath = "/app_worker/dataupdate";
        summarizers = new HashMap<String, List<String>>();
        List<String> nameUpdateSummarizers = new ArrayList<String>();
        nameUpdateSummarizers
                .add("org.waterforpeople.mapping.analytics.NameQuestionDataCleanser");
        summarizers.put("NameQuestionFix", nameUpdateSummarizers);

        List<String> questionUpdateSummarizers = new ArrayList<String>();
        questionUpdateSummarizers
                .add("org.waterforpeople.mapping.analytics.SurveyQuestionSummaryUpdater");
        summarizers.put("QuestionDataChange", questionUpdateSummarizers);

        List<String> accessPointUpdaters = new ArrayList<String>();
        accessPointUpdaters
                .add("org.waterforpeople.mapping.analytics.AccessPointUpdater");
        summarizers.put("AccessPointChange", accessPointUpdaters);

        List<String> accessPointSummaryUpdaters = new ArrayList<String>();
        accessPointSummaryUpdaters
                .add("org.waterforpeople.mapping.analytics.AccessPointStatusUpdater");
        accessPointSummaryUpdaters
                .add("org.waterforpeople.mapping.analytics.CommunityLocationSummarizer");
        summarizers.put("AccessPointSummaryChange", accessPointSummaryUpdaters);

        List<String> deviceQueueUpdaters = new ArrayList<String>();
        deviceQueueUpdaters.add("com.gallatinsystems.device.DeviceSurveyJobQueueUpdater");
        summarizers.put("DeviceSurveyJobQueueChange", deviceQueueUpdaters);

    }
}
