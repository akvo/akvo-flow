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

package org.waterforpeople.mapping.app.gwt.client.util;

import com.google.gwt.i18n.client.Constants;

public interface UploadConstants extends Constants {

	public String uploadUrl();

	public String s3Id();

	public String surveyDataS3Policy();

	public String surveyDataS3Sig();

	public String surveyDataS3Path();

	public String surveyDataContentType();

	public String imageS3Policy();

	public String imageS3Sig();

	public String imageS3Path();

	public String imageContentType();

	public String helpS3Policy();

	public String helpS3Sig();

	public String helpS3Path();

	public String videoContentType();
	
	public String apiKey();

}
