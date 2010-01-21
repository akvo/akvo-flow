package com.gallatinsystems.survey.device.xml;

import java.io.InputStream;

import com.gallatinsystems.survey.device.domain.Survey;

/**
 * interface for any class that can parse Survey definition xml files
 * 
 * @author Christopher Fagiani
 * 
 */
public interface SurveyParser {

    public Survey parse(InputStream inputStream);
}
