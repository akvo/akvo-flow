package org.waterforpeople.mapping.app.gwt.client.survey.view;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * interface for listeners that want to be notified on user click/selection of a
 * tree item
 * 
 * @author Christopher Fagiani
 * 
 */
public interface SurveyTreeListener {

	public void onSurveyTreeSelection(BaseDto dto);

}
