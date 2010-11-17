package org.waterforpeople.mapping.app.util;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

import com.gallatinsystems.survey.domain.Question;

/**
 * converts enumerated types
 * 
 * @author Christopher Fagiani
 * 
 */
@SuppressWarnings("unchecked")
public class TypeEnumConverter extends AbstractConverter {

	@Override
	protected Object convertToType(Class type, Object value) throws Throwable {
		if (value != null) {
			if (type == Question.Type.class) {
				return Question.Type.valueOf(value.toString());
			} else if (type == QuestionDto.QuestionType.class) {
				if (value != null)
					return QuestionDto.QuestionType.valueOf(value.toString());
				else{
					//return QuestionDto.QuestionType.valueOf(arg0)
				}
			}
		}
		return null;
	}

	@Override
	protected Class getDefaultType() {
		return QuestionType.class;
	}

}
