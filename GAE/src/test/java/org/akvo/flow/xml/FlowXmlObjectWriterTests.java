/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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

package test.java.org.akvo.flow.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.io.IOException;
import java.util.ArrayList;

import org.akvo.flow.xml.PublishedForm;
import org.akvo.flow.xml.XmlForm;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

class FlowXmlObjectWriterTests {


    @Test
    void testSerialiseMinimalForm() throws IOException {

        //Mock up a DTO tree
        SurveyDto dto1 = new SurveyDto();
        dto1.setKeyId(17L);
        dto1.setName("This is a form");
        dto1.setVersion("9.0");
        //QuestionGroup(s)
        ArrayList<QuestionGroupDto> gl = new ArrayList<>();
        dto1.setQuestionGroupList(gl);

        //Convert DTO to Jackson
        XmlForm form = new XmlForm(dto1);
        assertNotEquals(null, form);
        assertEquals("This is a form", form.getName());
        assertEquals("9.0", form.getVersion());

        //then into an XML string
        String xml = PublishedForm.generate(form);

        //And finally back to DTO again
        SurveyDto dto2 = PublishedForm.parse(xml, true).toDto(); //be strict

        assertNotEquals(null, dto2);
        assertEquals("This is a form", dto2.getName());

        //Exactly The same? (Platonic ideal - not necessary)
        assertEquals(dto1, dto2);
    }

}
