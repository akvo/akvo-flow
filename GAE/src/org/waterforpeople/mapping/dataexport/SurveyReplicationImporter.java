/*
 *  Copyright (C) 2010-2016 Stichting Akvo (Akvo Foundation)
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
import java.util.HashMap;
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
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.Survey.Status;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyGroup.ProjectType;

public class SurveyReplicationImporter {

    /**
     * copies one surveyGroup "Survey"
     * remaps all ids to make merging two instances safe
     * 
     * @param sourceBase
     * @param groupId
     * @param apiKey
     */
    public void importOneGroup(String sourceBase, long surveyId, String apiKey) {
        SurveyGroupDAO sgDao = new SurveyGroupDAO();
        SurveyDAO sDao = new SurveyDAO();
        QuestionGroupDao qgDao = new QuestionGroupDao();
        QuestionDao qDao = new QuestionDao();
        System.out.println("Importing one survey with id remapping from " + sourceBase);

        try {
            //First, find which group the survey is in
            List<SurveyGroup> allGroups = fetchSurveyGroups(sourceBase, apiKey); 
            for (SurveyGroup sg : allGroups) {
                System.out.println("surveygroup: " + sg.getName() + ":" + sg.getCode());
                if (sg.getProjectType() != ProjectType.PROJECT) {
                    continue; //skip looking in folders
                }
                boolean thisIsTheGroup = false;
                List<Survey> allSurveys = fetchSurveys(sg.getKey().getId(), sourceBase, apiKey); 
                for (Survey s0 : allSurveys) {
                    if ( s0.getKey().getId() == surveyId) {
                        thisIsTheGroup = true;  //Found it!
                        break;
                    }
                }                
                if (thisIsTheGroup) {
                    System.out.println(" copying group with " + allSurveys.size() + " surveys");
                    sg.setParentId(0L); //go in root folder
                    sg.setPath(""); //not used anymore
                    List<Long> nai = new ArrayList<Long>(1);
                    nai.add(0L);
                    sg.setAncestorIds(nai);
                    long oldId = sg.getKey().getId();
                    sg.setKey(null); //want new key
                    sgDao.save(sg);
                    long newId = sg.getKey().getId();
                    //if set, sg.newLocaleSurveyId is now wrong
                    
                    //Now copy everything inside it
                    //First, surveys (may be more than one for a monitored survey)
                    for (Survey s : allSurveys) {
                        System.out.println("  survey:" + s.getCode());

                        long oldSurveyId = s.getKey().getId();
                        s.setKey(null);//want a new key
                        List<Long> nai2 = new ArrayList<Long>(2);
                        nai2.add(0L);
                        nai2.add(newId);
                        s.setAncestorIds(nai2);
                        s.setSurveyGroupId(newId);
                        s.setPath("");
                        s.setStatus(Status.NOT_PUBLISHED); //not downloadable yet
                        s.setVersion(1.0);
                        sDao.save(s);
                        long newSurveyId = s.getKey().getId();
                        //Fix up newLocaleSurveyId
                        if (sg.getNewLocaleSurveyId() !=null && sg.getNewLocaleSurveyId() == oldSurveyId) {
                            sg.setNewLocaleSurveyId(newSurveyId);
                            sgDao.save(sg);
                        }

                        // The question groups
                        HashMap<Long,Long> qMap = new HashMap<Long,Long>(); //used to fix up dependency references
                        List<QuestionGroup> allQgs = fetchQuestionGroups(oldSurveyId, sourceBase, apiKey);
                        for (QuestionGroup qg : allQgs) {
                            System.out.println("     qg:" + qg.getCode());
                            long oldQgId = qg.getKey().getId();
                            qg.setKey(null); //want a new key
                            qg.setSurveyId(newSurveyId);
                            qg.setPath("");
                            qgDao.save(qg);
                            long newQgId = qg.getKey().getId();
                            // Now the questions
                            for (Question q : fetchQuestions(oldQgId, sourceBase, apiKey)) {
                                System.out.println("       q:" + q.getText());
                                q.setPath("");
                                long oldQId = q.getKey().getId();
                                q.setKey(null); //want a new key
                                qDao.save(q, newQgId); //options and other details are saved w the new question id
                                long newQId = q.getKey().getId();
                                qMap.put(oldQId, newQId);
                            }
                        }
                        // Now we know all question ids, so we can fix up their dependencies
                        System.out.println("     q dep fixup");
                        for (long qid : qMap.values()) {
                            Question q = qDao.getByKey(qid, true); //need all details
                            if (q == null) {System.out.println("     q not found:" + qid);continue;}
                            System.out.println("       q fixup:" + q.getText());
                            if (q.getDependentFlag() && q.getDependentQuestionId() != null) {
                                Long updatedId = qMap.get(q.getDependentQuestionId());
                                if (updatedId != null) {
                                    System.out.println("        dependency fixed up");
                                    q.setDependentQuestionId(updatedId);
                                    qDao.save(q, q.getQuestionGroupId());
                                }
                            }
                        }
                    }
                    break; //we can stop looking for the survey group 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    
    // surveyId == null copies all surveys, all forms
    public void executeImport(String sourceBase, Long surveyId, String apiKey) {
        if (surveyId != null) {
            importOneGroup(sourceBase, surveyId, apiKey);
            return;
        }
            
        SurveyGroupDAO sgDao = new SurveyGroupDAO();
        SurveyDAO sDao = new SurveyDAO();
//        HashMap<Long,Long> NewSurveyGroupIds = new HashMap<Long,Long>();
        QuestionGroupDao qgDao = new QuestionGroupDao();
        QuestionDao qDao = new QuestionDao();
//        HashMap<Long,Long> NewSurveyIds = new HashMap<Long,Long>();
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
