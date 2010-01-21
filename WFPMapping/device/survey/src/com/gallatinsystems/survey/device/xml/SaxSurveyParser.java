package com.gallatinsystems.survey.device.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
            parser.parse(inputStream, handler);
            return handler.getSurvey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
