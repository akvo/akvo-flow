/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.survey.device.view;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TabHost.TabContentFactory;

import com.gallatinsystems.survey.device.activity.SurveyViewActivity;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;

public abstract class SurveyTabContentFactory implements TabContentFactory {
	private static final int BUTTON_WIDTH = 150;

	private Button actionButton;
	private ScrollView scrollView;
	private String defaultLang;
	protected SurveyViewActivity context;
	protected SurveyDbAdapter databaseAdaptor;

	protected float defaultTextSize;
	protected String[] languageCodes;

	protected SurveyTabContentFactory(SurveyViewActivity c,
			SurveyDbAdapter dbAdaptor, float textSize, String defaultLang,
			String[] languageCodes) {
		context = c;
		databaseAdaptor = dbAdaptor;
		defaultTextSize = textSize;
		this.languageCodes = languageCodes;
		this.defaultLang = defaultLang;

	}

	protected ScrollView createSurveyTabContent() {
		scrollView = new ScrollView(context);
		scrollView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		return scrollView;
	}

	public Button configureActionButton(int text, OnClickListener handler) {
		actionButton = new Button(context);
		actionButton.setWidth(BUTTON_WIDTH);
		actionButton.setText(text);
		actionButton.setOnClickListener(handler);
		return actionButton;
	}

	/**
	 * resets the view by scrolling back to the top
	 */
	public void resetView() {
		if (scrollView != null) {
			scrollView.scrollTo(0, 0);
		}
	}

	/**
	 * replaces the current tab content with the view hierarchy passed in
	 * 
	 * @param content
	 */
	public View replaceViewContent(View content) {
		if (scrollView == null) {
			createSurveyTabContent();
		}
		scrollView.removeAllViews();
		if (content != null) {
			scrollView.addView(content);
		}
		return scrollView;
	}

	/**
	 * disables/enables submit button
	 * 
	 * @param isEnabled
	 */
	public void toggleButtons(boolean isEnabled) {
		if (actionButton != null) {
			actionButton.setEnabled(isEnabled);
		}
	}

	/**
	 * updates the language codes that this tab will use
	 * 
	 * @param langCodes
	 */
	protected void updateSelectedLanguages(String[] langCodes) {
		languageCodes = langCodes;
	}

	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}

	public String getDefaultLang() {
		return defaultLang;
	}

	protected SurveyViewActivity getContext() {
		return context;
	}

}
