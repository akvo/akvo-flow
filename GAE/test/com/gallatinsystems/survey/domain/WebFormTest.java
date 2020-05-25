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
package com.gallatinsystems.survey.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WebFormTest {
    
    final String secretKey = "very-secret-key";
    final String valueToEncrypt = "text";
    final Long surveyId = new Long(12345);
    final String pw = "9pyf-9a0k-htxq";
    final String webFormId = "R1dBTRhXXBMLA1kUClUSWw0GAVw";
    @Test
    void encryptSurveyId() {
        assertEquals(webFormId, WebForm.encryptId(surveyId, secretKey, pw)); 
    }
    
    @Test
    void decryptSurveyId() {
       assertEquals(surveyId+"$"+pw, WebForm.decryptId(webFormId, secretKey));
    }

    @Test
    void authId() {
       assertEquals("12345", WebForm.authId(webFormId, secretKey, pw));
    }

}