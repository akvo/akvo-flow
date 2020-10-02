package org.akvo.flow.api.app;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.akvo.flow.dao.DataPointAssignmentDao;
import org.akvo.flow.dao.SurveyAssignmentDao;
import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.akvo.flow.domain.persistent.SurveyAssignment;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.*;

public class DataUtil {


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
            qas.setQuestionID("0");
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
                SurveyInstance si = new SurveyInstance();
                si.setSurveyedLocaleId(dataPoint.getKey().getId());
                surveyInstances.add(si);
            }
        }
        return (List<SurveyInstance>) siDAO.save(surveyInstances);
    }

    public List<SurveyedLocale> createDataPoints(Long surveyId, int howMany) {
        final SurveyedLocaleDao dpDao = new SurveyedLocaleDao();
        final List<SurveyedLocale> datapoints = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            SurveyedLocale dataPoint = new SurveyedLocale();
            dataPoint.setIdentifier(String.valueOf(i));
            dataPoint.setSurveyGroupId(surveyId);
            dataPoint.setDisplayName("dataPoint: " + i);
            datapoints.add(dataPoint);
        }
        return new ArrayList<>(dpDao.save(datapoints));
    }

    public Set<Long> getEntityIds(List<? extends BaseDomain> entities) {
        Set<Long> entityIds = new HashSet<>();
        for (BaseDomain o : entities) {
            entityIds.add(o.getKey().getId());
        }
        return entityIds;
    }

    public Long randomId() {
        Random rnd = new Random();
        return rnd.nextLong();
    }

}
