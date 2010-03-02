package com.gallatinsystems.survey.domain;

import com.gallatinsystems.framework.domain.BaseDomain;

public class QuestionHelp extends BaseDomain{
	private String text;
	private String resourceUrl;
	
	public enum QuestionHelpType{
		TEXT,PICTURE_GALLERY,PICTURE,MOVIE
	}

}
