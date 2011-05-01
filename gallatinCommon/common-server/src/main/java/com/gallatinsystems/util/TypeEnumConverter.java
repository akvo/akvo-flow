package com.gallatinsystems.util;


import org.apache.commons.beanutils.converters.AbstractConverter;

import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.weightsmeasures.domain.UnitOfMeasure;

/**
 * converts enumerated types
 * 
 * @author Christopher Fagiani
 * 
 */
@SuppressWarnings("rawtypes")
public class TypeEnumConverter extends AbstractConverter {

	@Override
	protected Object convertToType(Class type, Object value) throws Throwable {
		if (value != null) {
			if (type == Question.Type.class) {
				return Question.Type.valueOf(value.toString());
			}  else if (type == UnitOfMeasure.UnitOfMeasureSystem.class) {
				return UnitOfMeasure.UnitOfMeasureSystem.valueOf(value
						.toString());
			} else if (type == UnitOfMeasure.UnitOfMeasureType.class) {
				return UnitOfMeasure.UnitOfMeasureType
						.valueOf(value.toString());
			} else if (type == QuestionHelpMedia.Type.class) {
				return QuestionHelpMedia.Type.valueOf(value.toString());
			} else if (type == Survey.Status.class){
				return Survey.Status.valueOf(value.toString());
			}
		}
		return null;
	}

	@Override
	public Object handleMissing(Class type) {
		return null;
	}

	@Override
	protected Class getDefaultType() {
		return  Question.Type.class;
	}

}
