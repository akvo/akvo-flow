/*
 * Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Flow.
 *
 * Akvo Flow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Flow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Flow.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.flow.rest.form;

import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.IOException;
import org.akvo.flow.api.app.DataStoreTestUtil;
import org.akvo.flow.xml.PublishedForm;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

class XmlFormAssemblerTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DataStoreTestUtil dataStoreTestUtil;

    @BeforeEach
    public void setUp() {
        helper.setUp();
        dataStoreTestUtil = new DataStoreTestUtil();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void assembleXmlFormShouldReturnEmptyIfException() throws IOException {
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(survey);

        XmlFormAssembler assembler = new XmlFormAssembler();

        try (MockedStatic<PublishedForm> mocked = mockStatic(PublishedForm.class)) {
            mocked.when(() -> PublishedForm.generate(any())).thenThrow(new IOException("Error"));
            assertThrows(IOException.class, () -> assembler.assembleXmlForm(survey, form));
        }
    }

    @Test
    public void assembleXmlFormShouldReturnCorrectValues() throws IOException {
        SurveyGroup survey = dataStoreTestUtil.createSurveyGroup();
        Survey form = dataStoreTestUtil.createSurvey(survey);

        XmlFormAssembler assembler = new XmlFormAssembler();

        try (MockedStatic<PublishedForm> mocked = mockStatic(PublishedForm.class)) {
            mocked.when(() -> PublishedForm.generate(any())).thenReturn("Form");
            FormUploadXml result = assembler.assembleXmlForm(survey, form);
            assertEquals("Form", result.getXmlContent());
            assertEquals(form.getObjectId() + "", result.getFormIdFilename());
            assertEquals(form.getObjectId() + "v1.0", result.getFormIdVersionFilename());
        }
    }
}
