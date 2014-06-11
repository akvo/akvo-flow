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

package com.gallatinsystems.survey.xml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.gallatinsystems.survey.domain.xml.Question;
import com.gallatinsystems.survey.domain.xml.QuestionGroup;
import com.gallatinsystems.survey.domain.xml.Survey;

/**
 * Utility to use JaxB to marshall/unmarshall Survey xml objects into/from xml strings.
 */
public class SurveyXMLAdapter {
    private static final Logger log = Logger.getLogger(SurveyXMLAdapter.class
            .getName());

    public Survey unmarshall(String xmlDoc) throws JAXBException {
        JAXBContext jc = JAXBContext
                .newInstance("com.gallatinsystems.survey.domain.xml");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        StringReader sr = new StringReader(xmlDoc);
        Survey survey = (Survey) unmarshaller.unmarshal(sr);
        return survey;
    }

    public String marshal(Survey survey) throws JAXBException {

        JAXBContext jc = JAXBContext
                .newInstance("com.gallatinsystems.survey.domain.xml");
        java.io.StringWriter sw = new java.io.StringWriter();
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(survey, sw);
        return sw.toString();
    }

    public String marshal(Question survey) throws JAXBException {

        JAXBContext jc = JAXBContext
                .newInstance("com.gallatinsystems.survey.domain.xml");
        java.io.StringWriter sw = new java.io.StringWriter();
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(survey, sw);
        return sw.toString();
    }

    public void printMarshalledDocument(Survey survey) {
        log.info(survey.toString());
        for (QuestionGroup og : survey.getQuestionGroup()) {
            log.info(og.toString());
            for (Question question : og.getQuestion()) {
                log.info(question.toString());
            }
        }
    }

    public static void main(String[] args) {
        SurveyXMLAdapter surveyXMLAdapter = new SurveyXMLAdapter();
        try {
            surveyXMLAdapter.unmarshall(readFileAsString(args[0]));
        } catch (JAXBException e) {
            log.log(Level.SEVERE, "Could not get survey from xml", e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not read file", e);
        }
    }

    private static String readFileAsString(String filePath)
            throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

}
