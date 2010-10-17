package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;

/**
 * interface that allows components to be notifed if a set of translations have
 * been updated.
 * 
 * @author Christopher Fagiani
 * 
 */
public interface TranslationChangeListener {

	/**
	 * method called when the set of translations has been updated (can include
	 * both saves and deletes)
	 * 
	 * @param translationList
	 */
	public void translationsUpdated(List<TranslationDto> translationList);
}
