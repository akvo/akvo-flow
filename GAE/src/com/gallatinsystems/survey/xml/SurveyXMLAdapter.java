package com.gallatinsystems.survey.xml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.gallatinsystems.survey.domain.xml.Question;
import com.gallatinsystems.survey.domain.xml.QuestionGroup;
import com.gallatinsystems.survey.domain.xml.Survey;

public class SurveyXMLAdapter {
	private static final Logger log = Logger.getLogger(SurveyXMLAdapter.class
			.getName());

	public Survey unmarshall(String xmlDoc) throws JAXBException {
		JAXBContext jc = JAXBContext
				.newInstance("com.gallatinsystems.survey.domain");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StringReader sr = new StringReader(xmlDoc);
		Survey survey = (Survey) unmarshaller.unmarshal(sr);
		return survey;
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
			log.log(Level.SEVERE,"Could not get survey from xml",e);
		} catch (IOException e) {
			log.log(Level.SEVERE,"Could not read file",e);
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
