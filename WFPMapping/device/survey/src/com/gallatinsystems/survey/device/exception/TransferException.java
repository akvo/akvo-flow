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

package com.gallatinsystems.survey.device.exception;

/**
 * exception class that allows for capturing of survey/instance ids
 * 
 * @author Christopher Fagiani
 * 
 */
public class TransferException extends Exception {

	private static final long serialVersionUID = -4649864250226025982L;
	private String surveyId;
	private Long instanceId;

	public TransferException(Exception e) {
		super(e);
	}

	public TransferException(String surveyId, Long instId, Exception cause) {
		super(cause);
		this.surveyId = surveyId;
		this.instanceId = instId;
	}

	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		if (surveyId != null) {
			builder.append("Survey id: ").append(surveyId)
					.append("\n");
		}
		if (instanceId != null) {
			builder.append("Instance id: ").append(instanceId.toString())
					.append("\n");
		}
		builder.append(super.getMessage());
		return builder.toString();
	}

}
