/*
 *  Copyright (C) 2020,2021 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.api.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.akvo.flow.dao.DataPointAssignmentDao;
import org.akvo.flow.dao.SurveyAssignmentDao;
import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.akvo.flow.domain.persistent.SurveyAssignment;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class DataStoreTestUtil {

    public static long mockedTime = 1601983649l;
    public static String mockedUUID = "TheMockedUuid";
    public static String mockedSubmitter = "TheHappySubmitter";
    public static long DEFAULT_REGISTRATION_FORM_ID = mockedTime*4;
    public static long DEFAULT_MONITORING_FORM_ID = mockedTime*2;

    public Device createDevice(Long deviceId, String androidId) {
        final DeviceDAO dao = new DeviceDAO();
        Key k = KeyFactory.createKey(Device.class.getSimpleName(), deviceId);
        Device d = new Device();
        d.setKey(k);
        d.setAndroidId(androidId);
        return dao.save(d);
    }

    public DataPointAssignment createDataPointAssignment(Long assignmentId, Long deviceId, List<Long> dataPointIds, Long surveyId) {
        final DataPointAssignment dpa = new DataPointAssignment();
        final DataPointAssignmentDao dao = new DataPointAssignmentDao();

        dpa.setDeviceId(deviceId);
        dpa.setDataPointIds(dataPointIds);
        dpa.setSurveyAssignmentId(assignmentId);
        dpa.setSurveyId(surveyId);

        return dao.save(dpa);
    }

    public SurveyAssignment createAssignment(Long surveyId, List<Long> deviceIds, List<Long> formIds) {
        final SurveyAssignment sa = new SurveyAssignment();
        final SurveyAssignmentDao dao = new SurveyAssignmentDao();

        sa.setSurveyId(surveyId);
        sa.setDeviceIds(deviceIds);
        sa.setFormIds(formIds);

        return dao.save(sa);
    }

    public List<QuestionAnswerStore> createAnswers(List<SurveyInstance> formInstances){
        QuestionAnswerStoreDao qasDAO = new QuestionAnswerStoreDao();
        final List<QuestionAnswerStore> answers = new ArrayList<>();
        for(SurveyInstance formInstance : formInstances){
            QuestionAnswerStore qas = new QuestionAnswerStore();
            qas.setSurveyInstanceId(formInstance.getKey().getId());
            qas.setQuestionID("12345");
            qas.setType("VALUE");
            qas.setValue("Random value: "+formInstance.getKey().getId());
            answers.add(qas);
        }
        return qasDAO.save(answers);
    }

    public List<SurveyInstance> createFormInstances(List<SurveyedLocale> dataPoints, int howMany){
        final List<SurveyInstance> surveyInstances = new ArrayList<>();
        SurveyInstanceDAO siDAO = new SurveyInstanceDAO();
        for (SurveyedLocale dataPoint: dataPoints) {
            for(int i = 0; i < howMany; i++) {
                SurveyInstance si = createSurveyInstance(dataPoint, i);
                surveyInstances.add(si);
            }
        }
        return (List<SurveyInstance>) siDAO.save(surveyInstances);
    }

    public SurveyInstance createSurveyInstance(SurveyedLocale dataPoint, int i) {
        SurveyInstance si = new SurveyInstance();
        si.setSurveyedLocaleId(dataPoint.getKey().getId());
        si.setSubmitterName(mockedSubmitter);
        si.setUuid(mockedUUID);
        if (i == 0) {
            long registrationFormId = dataPoint.getCreationSurveyId();
            si.setSurveyId(registrationFormId);
        } else {
            long monitoringFormId = DEFAULT_MONITORING_FORM_ID;
            si.setSurveyId(monitoringFormId);
        }
        Date date = new Date();
        date.setTime(mockedTime);
        si.setCollectionDate(date);
        return si;
    }

    public List<SurveyedLocale> createDataPoints(Long surveyId, int howMany) {
        return createDataPoints(surveyId, DEFAULT_REGISTRATION_FORM_ID, howMany);
    }

    public List<SurveyedLocale> createDataPoints(Long surveyId, Long registrationFormId, int howMany) {
        final SurveyedLocaleDao dpDao = new SurveyedLocaleDao();
        final List<SurveyedLocale> datapoints = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            SurveyedLocale dataPoint = createDataPoint(surveyId, registrationFormId, i);
            datapoints.add(dataPoint);
        }
        return new ArrayList<>(dpDao.save(datapoints));
    }

    public SurveyedLocale createDataPoint(Long surveyId, Long registrationFormId, int i) {
        SurveyedLocale dataPoint = new SurveyedLocale();
        dataPoint.setIdentifier("identifier-" + String.valueOf(i));
        dataPoint.setSurveyGroupId(surveyId);
        dataPoint.setDisplayName("dataPoint: " + i);
        dataPoint.setCreationSurveyId(registrationFormId);
        return dataPoint;
    }

    public Set<Long> getEntityIds(List<? extends BaseDomain> entities) {
        Set<Long> entityIds = new HashSet<>();
        for (BaseDomain o : entities) {
            entityIds.add(o.getKey().getId());
        }
        return entityIds;
    }

    public Long randomId() {
        return ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
    }

    public Collection<SurveyedLocale> saveDataPoints(List<SurveyedLocale> dataPoints) {
        return new SurveyedLocaleDao().save(dataPoints);
    }

    public Collection<SurveyInstance> saveFormInstances(List<SurveyInstance> formInstances) {
        return new SurveyInstanceDAO().save(formInstances);
    }

    public Translation createTranslation(Long surveyId, long parentId, Translation.ParentType parentType, String translationText, String languageCode) {
        Translation translation = new Translation();
        translation.setText(translationText);
        translation.setLanguageCode(languageCode);
        translation.setSurveyId(surveyId);
        translation.setParentId(parentId);
        translation.setParentType(parentType);
        return new TranslationDao().save(translation);
    }

    public SurveyGroup createSurveyGroup() {
        SurveyGroup sg = new SurveyGroup();
        return new SurveyGroupDAO().save(sg);
    }

    public Survey createSurvey(SurveyGroup newSg) {
        Survey survey = new Survey();
        survey.setName("Simple survey");
        survey.setSurveyGroupId(newSg.getKey().getId());
        return new SurveyDAO().save(survey);
    }

    public Survey createDefaultRegistrationForm(long surveyId) {
        Survey form = new Survey();
        form.setName("Simple survey");
        form.setKey(KeyFactory.createKey("Survey", DEFAULT_REGISTRATION_FORM_ID));
        form.setSurveyGroupId(surveyId);
        return new SurveyDAO().save(form);
    }

    public Survey createDefaultMonitoringForm(long surveyId) {
        Survey form = new Survey();
        form.setName("Simple monitoring survey");
        form.setKey(KeyFactory.createKey("Survey", DEFAULT_MONITORING_FORM_ID));
        form.setSurveyGroupId(surveyId);
        return new SurveyDAO().save(form);
    }

    public QuestionGroup createQuestionGroup(Survey newSurvey, int order, boolean immutable) {
        QuestionGroup qg = new QuestionGroup();
        qg.setName("question group");
        qg.setSurveyId(newSurvey.getKey().getId());
        qg.setOrder(order);
        qg.setRepeatable(false);
        qg.setImmutable(immutable);
        return new QuestionGroupDao().save(qg);
    }

    public Question createQuestion(Survey newSurvey, long questionGroupId, Question.Type type, boolean immutable) {
        Question q = new Question();
        q.setType(type);
        q.setQuestionGroupId(questionGroupId);
        q.setSurveyId(newSurvey.getKey().getId());
        q.setImmutable(immutable);
        q.setOrder(0);
        q.setDependentFlag(false);
        q.setTip("the tip");
        return new QuestionDao().save(q);
    }

    public QuestionOption createQuestionOption(Question question, String code, String text, int order) {
        QuestionOption questionOption = new QuestionOption();
        questionOption.setCode(code);
        questionOption.setText(text);
        questionOption.setQuestionId(question.getKey().getId());
        questionOption.setOrder(order);
        return new QuestionOptionDao().save(questionOption);
    }

    public Question createDependentQuestion(Survey newSurvey, Question dependent) {
        Question q = createQuestion(newSurvey, dependent.getQuestionGroupId(), Question.Type.FREE_TEXT, false);
        q.setDependentFlag(true);
        q.setOrder(1);
        q.setDependentQuestionId(dependent.getKey().getId());
        return new QuestionDao().save(q);
    }
}
