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

package com.gallatinsystems.survey.device.event;

import com.gallatinsystems.survey.device.view.QuestionView;

/**
 * event to be fired when the user interacts with a question in a significant
 * way.
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionInteractionEvent {

	public static final String TAKE_PHOTO_EVENT = "PHOTO";
	public static final String TAKE_VIDEO_EVENT = "VIDEO";
	public static final String GEO_CHECK_EVENT = "GEO";
	public static final String QUESTION_ANSWER_EVENT = "ANS";
	public static final String QUESTION_CLEAR_EVENT = "CLR";
	public static final String VIDEO_TIP_VIEW = "VIDTIP";
	public static final String PHOTO_TIP_VIEW = "PHOTOTIP";
	public static final String ACTIVITY_TIP_VIEW = "ACTIVITYTIP";
	public static final String SCAN_BARCODE_EVENT = "SCAN";
	public static final String START_TRACK = "START_TRACK";
	public static final String END_TRACK = "END_TRACK";
	

	private String eventType;
	private QuestionView source;

	public QuestionInteractionEvent(String type, QuestionView source) {
		this.eventType = type;
		this.source = source;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public QuestionView getSource() {
		return source;
	}

	public void setSource(QuestionView source) {
		this.source = source;
	}
}
