/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.dao.SurveyAssignmentDao;
import org.akvo.flow.domain.persistent.SurveyAssignment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataPointUtilTest {
    private final static LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @BeforeAll
    public static void setUp() {
        helper.setUp();
    }

    @AfterAll
    public static void tearDown() {
        helper.tearDown();
    }

    private long randomId() {
        return ThreadLocalRandom.current().nextLong(0, 1000000);
    }

    private int randomInt() {
        return ThreadLocalRandom.current().nextInt(1, 200);
    }

    private Survey createForm() {
        long id = randomId();
        Survey form = new Survey();
        form.setKey(KeyFactory.createKey("Survey", id));
        form.setName("Form" + id);
        form.setVersion(1.0d);
        SurveyDAO dao = new SurveyDAO();
        return dao.save(form);
    }

    private List<SurveyInstance> createFormInstances(Survey form, SurveyedLocale survey, int howMany) {
        List<SurveyInstance> newInstances = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            SurveyInstance si = new SurveyInstance();
            si.setSurveyId(form.getKey().getId());
            si.setSurveyedLocaleId(survey.getKey().getId());
            si.setCollectionDate(new Date());
            si.setUuid(UUID.randomUUID().toString());
            si.setFormVersion(form.getVersion());
            newInstances.add(si);
        }
        SurveyInstanceDAO dao = new SurveyInstanceDAO();
        return (List<SurveyInstance>) dao.save(newInstances);
    }

    private List<SurveyInstance> createFormInstances(SurveyedLocale dataPoint, Survey... forms) {
        List<SurveyInstance> allFormInstances = new ArrayList<>();
        for (Survey form : forms) {
            allFormInstances.addAll(createFormInstances(form, dataPoint, randomInt()));
        }
        return allFormInstances;
    }

    private SurveyedLocale createDataPoint() {
        long id = randomId();
        SurveyedLocale dp = new SurveyedLocale();
        dp.setKey(KeyFactory.createKey("SurveyedLocale", id));
        dp.setSurveyGroupId(randomId());

        SurveyedLocaleDao dao = new SurveyedLocaleDao();
        return dao.save(dp);
    }

    private Device createDevice() {
        long id = randomId();
        Device device = new Device();
        device.setKey(KeyFactory.createKey("Device", id));
        device.setEsn(String.valueOf(id));
        device.setDeviceType(Device.DeviceType.CELL_PHONE_ANDROID);
        device.setDeviceIdentifier(String.valueOf(id));

        DeviceDAO dao = new DeviceDAO();
        return dao.save(device);
    }

    private SurveyAssignment createAssignment(Long surveyId, List<Long> deviceIds, List<Long> formIds) {
        SurveyAssignment assignment = new SurveyAssignment();
        assignment.setKey(KeyFactory.createKey("SurveyAssignment", randomId()));
        assignment.setDeviceIds(deviceIds);
        assignment.setFormIds(formIds);
        assignment.setSurveyId(surveyId);

        SurveyAssignmentDao dao = new SurveyAssignmentDao();
        return dao.save(assignment);
    }

    private Set<String> uuidsFromDataPoints(List<SurveyedLocaleDto> datapointList) {
        Set<String> uuids = new HashSet<>();
        for (SurveyedLocaleDto dp : datapointList) {
            uuids.addAll(dp.getSurveyInstances()
                    .stream().map(SurveyInstanceDto::getUuid)
                    .collect(Collectors.toList()));
        }
        return uuids;
    }

    private Set<String> uuidsFromFormInstances(List<SurveyInstance> formInstances) {
        return formInstances.stream()
                .map(SurveyInstance::getUuid)
                .collect(Collectors.toSet());
    }

    @Test
    public void getSurveyInstancesReturnsFilteredData() {

        Survey form1 = createForm();
        Survey form2 = createForm();

        Device device = createDevice();

        SurveyedLocale dataPoint = createDataPoint();
        List<SurveyInstance> allFormInstances = createFormInstances(dataPoint, form1, form2);

        List<SurveyInstance> form1Instances = new ArrayList<>();
        for (SurveyInstance surveyInstance : allFormInstances) {
            if (surveyInstance.getSurveyId().equals(form1.getKey().getId())) {
                form1Instances.add(surveyInstance);
            }
        }

        // Assignment only contains Id for form1
        createAssignment(dataPoint.getSurveyGroupId(), Collections.singletonList(device.getKey().getId()), Collections.singletonList(form1.getKey().getId()));

        DataPointUtil dpu = new DataPointUtil();
        List<SurveyedLocaleDto> datapointList = dpu.getSurveyedLocaleDtosList(Collections.singletonList(dataPoint),
                dataPoint.getSurveyGroupId(), device);

        Set<String> expected = uuidsFromFormInstances(form1Instances);
        Set<String> actual = uuidsFromDataPoints(datapointList);

        assertEquals(expected, actual);
    }

    @Test
    public void getSurveyInstancesReturnsAllWhenDeviceIsNull() {
        Survey form1 = createForm();
        Survey form2 = createForm();

        SurveyedLocale dataPoint = createDataPoint();
        List<SurveyInstance> allFormInstances = createFormInstances(dataPoint, form1, form2);

        DataPointUtil dpu = new DataPointUtil();
        List<SurveyedLocaleDto> datapointList = dpu.getSurveyedLocaleDtosList(Collections.singletonList(dataPoint),
                dataPoint.getSurveyGroupId(), null);

        Set<String> expected = uuidsFromFormInstances(allFormInstances);
        Set<String> actual = uuidsFromDataPoints(datapointList);

        assertEquals(expected, actual);
    }
}
