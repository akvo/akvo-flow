package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyedLocaleServiceAsync {

	void deleteLocale(Long localeId, AsyncCallback<Void> callback);

	void listLocales(AccessPointSearchCriteriaDto searchCriteria,
			String cursorString,
			AsyncCallback<ResponseDto<ArrayList<SurveyedLocaleDto>>> callback);

}
