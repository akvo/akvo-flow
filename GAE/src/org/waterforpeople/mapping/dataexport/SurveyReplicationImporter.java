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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;

public class SurveyReplicationImporter {

    //TODO: remap ids to make merging two instances safe
    // surveyId == null copies all surveys, all forms
    public void executeImport(String sourceBase, Long surveyId, String apiKey) {
        SurveyGroupDAO sgDao = new SurveyGroupDAO();
        SurveyDAO sDao = new SurveyDAO();
        QuestionGroupDao qgDao = new QuestionGroupDao();
        QuestionDao qDao = new QuestionDao();
        boolean hasFoundSurvey = false;
        try {
            List<SurveyGroup> allGroups = fetchSurveyGroups(sourceBase, apiKey); 
            for (SurveyGroup sg : allGroups) {
                System.out.println("surveygroup: " + sg.getName() + ":"
                        + sg.getCode());
                if (surveyId == null) {//All surveys, so copy all groups, too
                    sgDao.save(sg);
                }
                for (Survey s : fetchSurveys(sg.getKey().getId(), sourceBase, apiKey)) {
                    System.out.println("  survey:" + s.getCode());
                    if (surveyId != null && surveyId.equals(s.getKey().getId())) {
                        //walk the ancestor id array to save all the ancestral groups
                        for (Long ancestor : s.listAncestorIds()) {
                            if (ancestor != 0 && ancestor != sg.getKey().getId()) {//Not root, not parent 
                                //Unsaved intermediate ancestor
                                for (SurveyGroup potentialAncestor : allGroups) {
                                    if (potentialAncestor.getKey().getId() == ancestor) {
                                        sgDao.save(potentialAncestor);
                                    }
                                }
                            }
                        }
                        sgDao.save(sg);//Immediate ancestor
                        sDao.save(s);
                        hasFoundSurvey = true;
                    } else if (surveyId != null) {
                        // if survey ID is not null but isn't matching, skip to
                        // next survey
                        continue;
                    } else {//All surveys
                        sDao.save(s);
                    }

                    for (QuestionGroup qg : fetchQuestionGroups(s.getKey()
                            .getId(), sourceBase, apiKey)) {
                        System.out.println("     qg:" + qg.getCode());
                        qgDao.save(qg);
                        for (Question q : fetchQuestions(qg.getKey().getId(),
                                sourceBase, apiKey)) {
                            System.out.println("       q" + q.getText());
                            qDao.save(q, qg.getKey().getId());
                        }
                    }
                    if (hasFoundSurvey) {
                        // if we've saved the survey we're looking for, we're
                        // done
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<SurveyGroup> fetchSurveyGroups(String serverBase, String apiKey)
            throws Exception {
        List<SurveyGroupDto> sgDtoList = BulkDataServiceClient
                .fetchSurveyGroups(serverBase, apiKey);
        List<SurveyGroup> sgList = new ArrayList<SurveyGroup>();
        return copyAndCreateList(sgList, sgDtoList, SurveyGroup.class);
    }

    public List<Survey> fetchSurveys(Long surveyGroupId, String serverBase, String apiKey)
            throws Exception {
        List<SurveyDto> surveyDtoList = BulkDataServiceClient.fetchSurveys(
                surveyGroupId, serverBase, apiKey);
        List<Survey> surveyList = new ArrayList<Survey>();
        return copyAndCreateList(surveyList, surveyDtoList, Survey.class);
    }

    public List<QuestionGroup> fetchQuestionGroups(Long surveyId,
            String serverBase, String apiKey) throws Exception {
        List<QuestionGroupDto> qgDtoList = BulkDataServiceClient
                .fetchQuestionGroups(serverBase, surveyId.toString(), apiKey);
        List<QuestionGroup> qgList = new ArrayList<QuestionGroup>();
        return copyAndCreateList(qgList, qgDtoList, QuestionGroup.class);
    }

    public List<Question> fetchQuestions(Long questionGroupId, String serverBase, String apiKey)
            throws Exception {
        List<QuestionDto> qgDtoList = BulkDataServiceClient.fetchQuestions(
                serverBase, questionGroupId, apiKey);
        List<Question> qList = new ArrayList<Question>();

        SurveyServiceImpl ssi = new SurveyServiceImpl();
        for (QuestionDto dto : qgDtoList) {
            QuestionDto dtoDetail = null;
            for (int i = 0; i < 3; i++) {
                try {
                    dtoDetail = BulkDataServiceClient
                            .loadQuestionDetails(serverBase, dto.getKeyId(), apiKey);
                    break;
                } catch (IOException iex) {
                    System.out.print("Retrying because of timeout.");
                }
            }
            Question q = ssi.marshalQuestion(dtoDetail);
            qList.add(q);
        }
        return qList;
    }

    public static <T extends BaseDomain, U extends BaseDto> List<T> copyAndCreateList(
            List<T> canonicalList, List<U> dtoList, Class<T> clazz) {

        for (U dto : dtoList) {
            T canonical = null;
            try {
                canonical = clazz.newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            DtoMarshaller.copyToCanonical(canonical, dto);

            if (canonical instanceof Survey && dto instanceof SurveyDto) {
                Survey s = (Survey) canonical;
                SurveyDto d = (SurveyDto) dto;
                // overwrite status for published surveys to unpublished
                if (Survey.Status.PUBLISHED.equals(s.getStatus())) {
                    s.setStatus(Survey.Status.NOT_PUBLISHED);
                }

                // mismatch in SurveyDto and Survey property names
                s.setDesc(d.getDescription());
            }
            canonicalList.add(canonical);
        }
        return canonicalList;
    }

}
