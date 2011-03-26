package org.waterforpeople.mapping.app.util;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.UnitOfMeasureDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.web.dto.OGRFeatureDto;
import org.waterforpeople.mapping.domain.AccessPoint;

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
@SuppressWarnings("unchecked")
public class TypeEnumConverter extends AbstractConverter {

	@Override
	protected Object convertToType(Class type, Object value) throws Throwable {
		if (value != null) {
			if (type == Question.Type.class) {

				return Question.Type.valueOf(value.toString());

			} else if (type == QuestionDto.QuestionType.class) {

				return QuestionDto.QuestionType.valueOf(value.toString());

			} else if (type == AccessPoint.Status.class) {

				return AccessPoint.Status.valueOf(value.toString());

			} else if (type == AccessPointDto.Status.class) {
				return AccessPointDto.Status.valueOf(value.toString());
			} else if (type == AccessPoint.AccessPointType.class) {
				return AccessPoint.AccessPointType.valueOf(value.toString());
			} else if (type == AccessPointDto.AccessPointType.class) {
				return AccessPointDto.AccessPointType.valueOf(value.toString());
			} else if (type == UnitOfMeasure.UnitOfMeasureSystem.class) {
				return UnitOfMeasure.UnitOfMeasureSystem.valueOf(value
						.toString());
			} else if (type == UnitOfMeasureDto.UnitOfMeasureSystem.class) {
				return UnitOfMeasureDto.UnitOfMeasureSystem.valueOf(value
						.toString());
			} else if (type == UnitOfMeasure.UnitOfMeasureType.class) {
				return UnitOfMeasure.UnitOfMeasureType
						.valueOf(value.toString());
			} else if (type == UnitOfMeasureDto.UnitOfMeasureType.class) {
				return UnitOfMeasureDto.UnitOfMeasureType.valueOf(value
						.toString());
			} else if (type == QuestionHelpMedia.Type.class) {
				return QuestionHelpMedia.Type.valueOf(value.toString());
			} else if (type == QuestionHelpDto.Type.class) {
				return QuestionHelpDto.Type.valueOf(value.toString());
			} else if (type == OGRFeatureDto.FeatureType.class) {
				return OGRFeatureDto.FeatureType.valueOf(value.toString());
			}else if (type == Survey.Status.class){
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
		return QuestionType.class;
	}

}
