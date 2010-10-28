package org.waterforpeople.mapping.app.util;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.gallatinsystems.survey.domain.Question;

public class TypeEnumConverter extends AbstractConverter{
	
	@Override
	protected Object convertToType(Class type, Object value) throws Throwable {
		if(value != null){
			if(type == Question.Type.class){
				return Question.Type.valueOf(value.toString());
			}else if (type == QuestionDto.QuestionType.class){
				return QuestionDto.QuestionType.valueOf(value.toString());
			}		
		}
		return null;
	}

	@Override
	protected Class getDefaultType() {
		return QuestionType.class;
	}

}
