package org.waterforpeople.mapping.app.util;

import java.util.Locale;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.UnitOfMeasureDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.web.dto.OGRFeatureDto;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.weightsmeasures.domain.UnitOfMeasure;
import com.google.appengine.api.datastore.Text;

public class DtoMarshaller extends com.gallatinsystems.util.DtoMarshaller {

	
	/**
	 * sets up the converters that this marshaller should use
	 */
	protected void configureConverters() {		
		String pattern = "MM/dd/yy";
		Locale locale = Locale.getDefault();
		DateLocaleConverter converter = new DateLocaleConverter(locale, pattern);
		converter.setLenient(true);
		ConvertUtils.register(converter, java.util.Date.class);

		TypeEnumConverter enumConverter = new TypeEnumConverter();
		ConvertUtils.register(enumConverter, Question.Type.class);
		ConvertUtils.register(enumConverter, QuestionDto.QuestionType.class);	
		ConvertUtils.register(enumConverter,AccessPoint.Status.class);
		ConvertUtils.register(enumConverter,AccessPointDto.Status.class);
		ConvertUtils.register(enumConverter,AccessPoint.AccessPointType.class);
		ConvertUtils.register(enumConverter,AccessPointDto.AccessPointType.class);
		ConvertUtils.register(enumConverter,UnitOfMeasure.UnitOfMeasureSystem.class);
		ConvertUtils.register(enumConverter,UnitOfMeasureDto.UnitOfMeasureSystem.class);
		ConvertUtils.register(enumConverter,UnitOfMeasure.UnitOfMeasureType.class);
		ConvertUtils.register(enumConverter,UnitOfMeasureDto.UnitOfMeasureType.class);
		ConvertUtils.register(enumConverter,QuestionHelpMedia.Type.class);
		ConvertUtils.register(enumConverter,QuestionHelpDto.Type.class);
		ConvertUtils.register(enumConverter, OGRFeatureDto.FeatureType.class);
		ConvertUtils.register(enumConverter, Survey.Status.class);
		
		
		DatastoreTextConverter textConverter = new DatastoreTextConverter();
		ConvertUtils.register(textConverter,Text.class);				
		ConvertUtils.register(textConverter,String.class);
	}

}
