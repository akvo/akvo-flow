/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.device.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import com.gallatinsystems.survey.device.domain.Survey;

/**
 * implementation of the SurveyParser using the Simple Api for XML (SAX). This
 * will use a SurveyHandler to process the XML document in a streaming fashion.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SaxSurveyParser implements SurveyParser {

    public Survey parse(InputStream inputStream) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            SurveyHandler handler = new SurveyHandler();   
            Reader reader = new InputStreamReader(inputStream,"UTF-8");
            InputSource source = new InputSource(reader);
            source.setEncoding("UTF-8");            
            parser.parse(source, handler);
            return handler.getSurvey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
