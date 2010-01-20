package com.gallatinsystems.survey.device.xml;

import java.io.InputStream;

import com.gallatinsystems.survey.device.domain.Survey;

public interface SurveyParser {

	public Survey parse(InputStream inputStream);
}
