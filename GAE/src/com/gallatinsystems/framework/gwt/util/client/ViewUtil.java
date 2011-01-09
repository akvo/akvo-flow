package com.gallatinsystems.framework.gwt.util.client;

import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * simple utility for use client-side via GWT
 * 
 * @author Christopher Fagiani
 *
 */
public class ViewUtil {

	/**
	 * returns true if the text box passed in contains at least 1 non-whitespace character
	 * @param box
	 * @return
	 */
	public static boolean isTextPopulated(TextBoxBase box){
		boolean populated = true;
		if(box == null || box.getText() == null || box.getText().trim().length()<=0){
			populated = false;
		}
		return populated;
	}
}
