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
import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.akvo.flow.xml.PublishedForm;
import org.akvo.flow.xml.XmlForm;

public class XmlFormAssembler {

    public XmlFormAssembler() {
    }

    @Nonnull
    FormUploadXml assembleXmlForm(SurveyGroup survey, Survey form) throws IOException {
        Properties props = System.getProperties();
        String alias = props.getProperty("alias");
        String xmlAppId = props.getProperty("xmlAppId");
        String appStr = (xmlAppId != null && !xmlAppId.isEmpty()) ? xmlAppId : SystemProperty.applicationId.get();
        XmlForm jacksonForm = new XmlForm(form, survey, appStr, alias);
        Long formId = form.getObjectId();
        return new FormUploadXml(Long.toString(formId), //latest version in plain filename
                formId + "v" + form.getVersion(), //archive copy
                PublishedForm.generate(jacksonForm));
    }
}
