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

import com.gallatinsystems.survey.domain.Translation;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
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
import com.gallatinsystems.survey.domain.Survey.Status;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyGroup.ProjectType;

public class SurveyReplicationImporter {

    private static final Logger log = Logger.getLogger(SurveyReplicationImporter.class.getName());

    /**
     * copies one surveyGroup "Survey" remaps all ids to make merging two instances safe
     *
     * @param sourceBase
     * @param surveyId
     * @param apiKey
     */
    public void importOneGroup(String sourceBase, long surveyId, String apiKey) {
        SurveyGroupDAO sgDao = new SurveyGroupDAO();
        SurveyDAO sDao = new SurveyDAO();
        QuestionGroupDao qgDao = new QuestionGroupDao();
        QuestionDao qDao = new QuestionDao();
        System.out
                .println("Importing survey " + surveyId + " with id remapping from " + sourceBase);

        int count_sg = 0, count_s = 0, count_qg = 0, count_q = 0;
        try {
            // First, find which group the survey is in
            List<SurveyGroup> allGroups = fetchSurveyGroups(sourceBase, apiKey);
            System.out.println(" scanning " + allGroups.size() + " survey groups");
            for (SurveyGroup sg : allGroups) {
                System.out.println(" surveygroup: " + sg.getKey().getId() + " " + sg.getName()
                        + ":" + sg.getCode());
                count_sg++;
                if (sg.getProjectType() != ProjectType.PROJECT) {
                    continue; // skip looking in folders
                }
                boolean thisIsTheGroup = false;
                List<Survey> allSurveys = fetchSurveys(sg.getKey().getId(), sourceBase, apiKey);
                for (Survey s0 : allSurveys) {
                    if (s0.getKey().getId() == surveyId) {
                        thisIsTheGroup = true; // Found it!
                        break;
                    }
                }
                if (thisIsTheGroup) {
                    System.out.println(" copying group with " + allSurveys.size() + " surveys");
                    sg.setParentId(0L); // go in root folder
                    sg.setPath(""); // not used anymore
                    List<Long> nai = new ArrayList<Long>(1);
                    nai.add(0L);
                    sg.setAncestorIds(nai);
                    sg.getKey().getId();
                    sg.setKey(null); // want new key
                    sgDao.save(sg);
                    long newId = sg.getKey().getId();
                    // if set, sg.newLocaleSurveyId is now wrong

                    // Now copy everything inside it
                    // First, surveys (may be more than one for a monitored survey)
                    for (Survey s : allSurveys) {
                        int numberOfTranslations = s.getTranslationMap() == null ? 0 : s.getTranslationMap().size();
                        System.out.println("  survey:" + s.getKey().getId() + " " + s.getCode()+ " with " + numberOfTranslations + " translations");

                        long oldSurveyId = s.getKey().getId();
                        s.setKey(null);// want a new key
                        List<Long> nai2 = new ArrayList<Long>(2);
                        nai2.add(0L);
                        nai2.add(newId);
                        s.setAncestorIds(nai2);
                        s.setSurveyGroupId(newId);
                        s.setPath("");
                        s.setStatus(Status.NOT_PUBLISHED); // not downloadable yet
                        s.setVersion(1.0);
                        sDao.save(s);
                        long newSurveyId = s.getKey().getId();
                        sDao.saveTranslations(s);
                        // Fix up newLocaleSurveyId
                        if (sg.getNewLocaleSurveyId() != null
                                && sg.getNewLocaleSurveyId() == oldSurveyId) {
                            System.out.println("   registration form id fixup :" + oldSurveyId
                                    + " -> " + newSurveyId);
                            sg.setNewLocaleSurveyId(newSurveyId);
                            sgDao.save(sg);
                        }
                        count_s++;

                        // The question groups
                        HashMap<Long, Long> qMap = new HashMap<Long, Long>(); // used to fix up
                                                                              // dependency
                                                                              // references
                        List<QuestionGroup> allQgs = fetchQuestionGroups(oldSurveyId, sourceBase,
                                apiKey);
                        for (QuestionGroup qg : allQgs) {
                            int numberOfTranslationsGr = qg.getTranslations() == null? 0: qg.getTranslations().size();
                            System.out.println("     qg:" + qg.getKey().getId() + " " + qg.getCode() + "with " + numberOfTranslationsGr + " translations");
                            long oldQgId = qg.getKey().getId();
                            qg.setKey(null); // want a new key
                            qg.setSurveyId(newSurveyId);
                            qg.setPath("");
                            qgDao.save(qg);
                            long newQgId = qg.getKey().getId();
                            qgDao.saveGroupTranslations(qg);
                            // Now the questions
                            for (Question q : fetchQuestions(oldQgId, sourceBase, apiKey)) {
                                System.out.println("       q:" + q.getKey().getId() + " "
                                        + q.getText());
                                q.setPath("");
                                long oldQId = q.getKey().getId();
                                q.setKey(null); // want a new key
                                qDao.save(q, newQgId); // options and other details are saved w the
                                                       // new question id
                                long newQId = q.getKey().getId();
                                qMap.put(oldQId, newQId);
                                count_q++;
                            }
                            count_qg++;
                        }
                        // Now we know all question ids, so we can fix up their dependencies
                        System.out.println("     q depependency fixup pass");
                        for (long qid : qMap.values()) {
                            Question q = qDao.getByKey(qid); // need no details
                            if (q == null) {
                                System.out.println("     q not found:" + qid);
                                continue;
                            }
                            if (q.getDependentFlag() && q.getDependentQuestionId() != null) {
                                Long updatedId = qMap.get(q.getDependentQuestionId());
                                if (updatedId != null) {
                                    System.out.println("       q:" + q.getText());
                                    System.out.println("        dep fixup :"
                                            + q.getDependentQuestionId() + " -> " + updatedId);
                                    q.setDependentQuestionId(updatedId);
                                    qDao.save(q, q.getQuestionGroupId());
                                }
                            }
                        }
                    }
                    break; // we can stop looking for the survey group
                }
            }
            System.out.println("Survey import complete after " + count_sg + " groups scanned; "
                    + count_s + " surveys, " + count_qg + " question groups, " + count_q
                    + " questions copied. ");
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "Error copying survey", e);
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
        // HashMap<Long,Long> NewSurveyGroupIds = new HashMap<Long,Long>();
        QuestionGroupDao qgDao = new QuestionGroupDao();
        QuestionDao qDao = new QuestionDao();
        // HashMap<Long,Long> NewSurveyIds = new HashMap<Long,Long>();
        boolean hasFoundSurvey = false;
        try {
            List<SurveyGroup> allGroups = fetchSurveyGroups(sourceBase, apiKey);
            for (SurveyGroup sg : allGroups) {
                System.out.println("surveygroup: " + sg.getName() + ":"
                        + sg.getCode());
                if (surveyId == null) {// All surveys, so copy all groups, too
                    sgDao.save(sg);
                }
                for (Survey s : fetchSurveys(sg.getKey().getId(), sourceBase, apiKey)) {
                    System.out.println("  survey:" + s.getCode());
                    if (surveyId != null && surveyId.equals(s.getKey().getId())) {
                        // walk the ancestor id array to save all the ancestral groups
                        for (Long ancestor : s.listAncestorIds()) {
                            if (ancestor != 0 && ancestor != sg.getKey().getId()) {// Not root, not
                                                                                   // parent
                                // Unsaved intermediate ancestor
                                for (SurveyGroup potentialAncestor : allGroups) {
                                    if (potentialAncestor.getKey().getId() == ancestor) {
                                        sgDao.save(potentialAncestor);
                                    }
                                }
                            }
                        }
                        sgDao.save(sg);// Immediate ancestor
                        sDao.save(s);
                        hasFoundSurvey = true;
                    } else if (surveyId != null) {
                        // if survey ID is not null but isn't matching, skip to
                        // next survey
                        continue;
                    } else {// All surveys
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
        return copyAndCreateGroupList(qgDtoList);
    }

    private List<QuestionGroup> copyAndCreateGroupList(List<QuestionGroupDto> qgDtoList) {
        List<QuestionGroup> groups = new ArrayList<>();
        if (qgDtoList != null) {
            for (QuestionGroupDto dto : qgDtoList) {
                QuestionGroup group = new QuestionGroup();
                DtoMarshaller.copyToCanonical(group, dto);
                Map<String, TranslationDto> translationMap = dto.getTranslationMap();
                if (translationMap != null) {
                    HashMap<String, Translation> mappedTranslations = mapTranslations(translationMap);
                    group.setTranslations(mappedTranslations);
                }
            }
        }
        return groups;
    }

    private static HashMap<String, Translation> mapTranslations(Map<String, TranslationDto> translationMap) {
        HashMap<String, Translation> translationHashMap = new HashMap<>();
        for (TranslationDto dto: translationMap.values()) {
            Translation t = new Translation();
                t.setKey((KeyFactory.createKey(
                        Translation.class.getSimpleName(), dto.getKeyId())));
            t.setLanguageCode(dto.getLangCode());
            t.setText(dto.getText());
            t.setParentId(dto.getParentId());
            if (Translation.ParentType.SURVEY_NAME.toString().equals(dto.getParentType())) {
                t.setParentType(Translation.ParentType.SURVEY_NAME);
            } else if (Translation.ParentType.SURVEY_DESC.toString().equals(dto.getParentType())) {
                t.setParentType(Translation.ParentType.SURVEY_DESC);
            } else if (Translation.ParentType.QUESTION_GROUP_DESC.toString().equals(dto.getParentType())) {
                t.setParentType(Translation.ParentType.QUESTION_GROUP_DESC);
            } else if (Translation.ParentType.QUESTION_GROUP_NAME.toString().equals(dto.getParentType())) {
                t.setParentType(Translation.ParentType.QUESTION_GROUP_NAME);
            }
            translationHashMap.put(dto.getLangCode(), t);
        }
        return null;
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
                    dtoDetail = BulkDataServiceClient.loadQuestionDetails(serverBase, dto.getKeyId(), apiKey);
                    break;
                } catch (IOException iex) {
                    System.out.print("Retrying because of timeout.");
                    log.log(Level.SEVERE, "Error fetchQuestions", iex);
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
                e.printStackTrace();
                log.log(Level.SEVERE, "Error copyAndCreateList", e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                log.log(Level.SEVERE, "Error copyAndCreateList", e);
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
                if (d.getTranslationMap() != null) {
                    s.setTranslationMap(mapTranslations(d.getTranslationMap()));
                }
            }
            canonicalList.add(canonical);
        }
        return canonicalList;
    }

}
