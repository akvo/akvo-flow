/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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
package org.waterforpeople.mapping.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.response.FormInstance;
import org.waterforpeople.mapping.domain.response.Response;

public class SurveyInstanceHandler {
    private static final Logger log = Logger.getLogger(SurveyInstanceHandler.class.getName());
    
    public SurveyInstance fromJSON(String data) {
        FormInstance formInstance = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            formInstance = mapper.readValue(data, FormInstance.class);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error mapping JSON data: " + e.getMessage(), e);
            return null;
        }
        
        SurveyInstance si = new SurveyInstance();
        si.setUserID(1L);
        si.setCollectionDate(new Date(formInstance.getSubmissionDate()));
        si.setSubmitterName(formInstance.getUsername());
        si.setDeviceIdentifier(formInstance.getDeviceId());
        si.setSurveyalTime(formInstance.getDuration());
        si.setSurveyedLocaleIdentifier(formInstance.getDataPointId());
        si.setSurveyId(formInstance.getFormId());
        si.setUuid(formInstance.getUUID());
        si.setQuestionAnswersStore(new ArrayList<QuestionAnswerStore>());
        
        // Process form responses
        for (Response response : formInstance.getResponses()) {
            QuestionAnswerStore qas = new QuestionAnswerStore();
            qas.setSurveyId(si.getSurveyId());
            qas.setQuestionID(response.getQuestionId());
            qas.setCollectionDate(si.getCollectionDate());
            qas.setType(response.getAnswerType());
            qas.setValue(response.getValue());
            
            si.getQuestionAnswersStore().add(qas);
        }
        
        return si;
    }
    
    public SurveyInstance fromTSV(List<String> data) {
        final SurveyInstance si = new SurveyInstance();
        si.setUserID(1L);
        si.setQuestionAnswersStore(new ArrayList<QuestionAnswerStore>());
        
        boolean first = true;
        for (String line : data) {
            final String[] parts = line.split("\t");
            if (parts.length < 12) {
                return null;
            }
            
            if (first) {
                try {
                    si.setSurveyId(Long.parseLong(parts[0].trim()));
                    si.setCollectionDate(new Date(new Long(parts[7].trim())));
                } catch (NumberFormatException e) {
                    log.log(Level.SEVERE, "Could not parse line: " + line, e);
                    return null;
                }
                si.setSubmitterName(parts[5].trim());
                si.setDeviceIdentifier(parts[8].trim());
                si.setUuid(parts[11].trim());
                
                // Time and LocaleID. Old app versions might not include these columns.
                if (parts.length > 12 && si.getSurveyalTime() == null) {
                    try {
                        si.setSurveyalTime(Long.valueOf(parts[12].trim()));
                    } catch (NumberFormatException e) {
                        log.log(Level.WARNING, "Surveyal time column is not a number", e);
                    }
                }
                if (parts.length > 13 && si.getSurveyedLocaleIdentifier() == null) {
                    si.setSurveyedLocaleIdentifier(parts[13].trim());
                }
                
                first = false;
            }

            QuestionAnswerStore qas = new QuestionAnswerStore();
            qas.setSurveyId(si.getSurveyId());
            qas.setQuestionID(parts[2].trim());
            qas.setType(parts[3].trim());
            qas.setCollectionDate(si.getCollectionDate());
            qas.setValue(parts[4].trim());
            qas.setScoredValue(parts[9].trim());
            qas.setStrength(parts[10].trim());
            si.getQuestionAnswersStore().add(qas);
        }

        return si;
    }
}
