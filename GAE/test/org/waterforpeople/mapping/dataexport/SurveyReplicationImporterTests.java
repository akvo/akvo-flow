/*
 *  Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.survey.domain.QuestionGroup;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SurveyReplicationImporterTests {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private String DTO_QG_JSON_LIST = "{\"resultCount\":0,\"message\":null,\"code\":null,\"offset\":0,\"cursor\":null,\"url\":null,\"dtoList\":[{\"keyId\":146642013,\"questionMap\":null,\"code\":\"General\",\"surveyId\":145492013,\"order\":1,\"path\":\"\",\"name\":\"General\",\"sourceId\":null,\"repeatable\":false,\"status\":null,\"immutable\":null,\"translationMap\":{\"es\":{\"keyId\":6191349976006656,\"langCode\":\"es\",\"text\":\"General\",\"parentId\":146642013,\"parentType\":\"QUESTION_GROUP_NAME\",\"surveyId\":145492013,\"questionGroupId\":146642013}},\"displayName\":\"General\"},{\"keyId\":148442015,\"questionMap\":null,\"code\":\"Asset data\",\"surveyId\":145492013,\"order\":2,\"path\":\"\",\"name\":\"Asset data\",\"sourceId\":null,\"repeatable\":false,\"status\":null,\"immutable\":null,\"translationMap\":{\"es\":{\"keyId\":4644886871539712,\"langCode\":\"es\",\"text\":\"Datos sobre lo que sea\",\"parentId\":148442015,\"parentType\":\"QUESTION_GROUP_NAME\",\"surveyId\":145492013,\"questionGroupId\":148442015}},\"displayName\":\"Asset data\"},{\"keyId\":144682013,\"questionMap\":null,\"code\":\"Functionality\",\"surveyId\":145492013,\"order\":3,\"path\":\"\",\"name\":\"Functionality\",\"sourceId\":null,\"repeatable\":false,\"status\":null,\"immutable\":null,\"translationMap\":{\"es\":{\"keyId\":5770786778382336,\"langCode\":\"es\",\"text\":\"Funcionalidad\",\"parentId\":144682013,\"parentType\":\"QUESTION_GROUP_NAME\",\"surveyId\":145492013,\"questionGroupId\":144682013}},\"displayName\":\"Functionality\"},{\"keyId\":147442013,\"questionMap\":null,\"code\":\"Service level\",\"surveyId\":145492013,\"order\":4,\"path\":\"\",\"name\":\"Service level\",\"sourceId\":null,\"repeatable\":false,\"status\":null,\"immutable\":null,\"translationMap\":{\"es\":{\"keyId\":5207836824961024,\"langCode\":\"es\",\"text\":\"Nivel servicio\",\"parentId\":147442013,\"parentType\":\"QUESTION_GROUP_NAME\",\"surveyId\":145492013,\"questionGroupId\":147442013}},\"displayName\":\"Service level\"}]}";

    @BeforeEach
    public void setUp() {
        helper.setUp();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    void testCopyAndCreateDtosToCanonical() throws Exception {
        List<QuestionGroupDto> qgDtoList = BulkDataServiceClient.parseQuestionGroups(DTO_QG_JSON_LIST);
        List<QuestionGroup> qgList = new ArrayList<QuestionGroup>();
        List<QuestionGroup> qgs = SurveyReplicationImporter.copyAndCreateList(qgList, qgDtoList, QuestionGroup.class);

        assertNotEquals(null, qgList);
        assertEquals(4, qgList.size());

        QuestionGroupDto qgWithTranslations =  qgDtoList.get(1);
        Map<String, TranslationDto> translationDtoMap = qgWithTranslations.getTranslationMap();

        assertTrue(translationDtoMap.containsKey("es"));

        TranslationDto translationDto = translationDtoMap.get("es");
        assertEquals("Datos sobre lo que sea", translationDto.getText());
    }
}
