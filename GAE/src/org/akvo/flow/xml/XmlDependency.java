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

package org.akvo.flow.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/*
 * Class for working with form XML like this:
 * <dependency answer-value="Other" question="536189117"/>
 */

public class XmlDependency {

    @JacksonXmlProperty(localName = "question", isAttribute = true)
    private long question;
    @JacksonXmlProperty(localName = "answer-value", isAttribute = true)
    private String answerValue;

    public XmlDependency() {
    }

    @Override public String toString() {
        return "dependency{" +
                "question='" + question + '\'' +
                "answerValue='" + answerValue + '\'' +
                '}';
    }

}
