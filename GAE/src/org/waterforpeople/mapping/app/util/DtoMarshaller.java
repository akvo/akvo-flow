package org.waterforpeople.mapping.app.util;

import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.survey.domain.Question;
import com.google.appengine.api.datastore.KeyFactory;

public class DtoMarshaller {

	public static <T extends BaseDomain, U extends BaseDto> void copyToCanonical(
			T canonical, U dto) {
		try {
			String pattern = "MM/dd/yy";
			Locale locale = Locale.getDefault();
			DateLocaleConverter converter = new DateLocaleConverter(locale,
					pattern);
			converter.setLenient(true);
			ConvertUtils.register(converter, java.util.Date.class);
			TypeEnumConverter enumConverter = new TypeEnumConverter();
			ConvertUtils.register(enumConverter, Question.Type.class);
			ConvertUtils.register(enumConverter, QuestionDto.QuestionType.class);
			BeanUtils.copyProperties(canonical, dto);
			if (dto.getKeyId() != null) {
				// by default, the JDO key kind uses the Simple name
				canonical.setKey(KeyFactory.createKey(canonical.getClass()
						.getSimpleName(), dto.getKeyId()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static <T extends BaseDomain, U extends BaseDto> void copyToDto(
			T canonical, U dto) {
		try {
			String pattern = "MM/dd/yy";
			Locale locale = Locale.getDefault();
			DateLocaleConverter converter = new DateLocaleConverter(locale,
					pattern);
			converter.setLenient(true);
			ConvertUtils.register(converter, java.util.Date.class);
			BeanUtils.copyProperties(dto, canonical);
			if (canonical.getKey() != null) {
				dto.setKeyId(canonical.getKey().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
