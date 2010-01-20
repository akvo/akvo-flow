package com.gallatinsystems.survey.device.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.gallatinsystems.survey.device.domain.Survey;

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
