/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.dto;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.device.domain.DeviceFileJobQueue;
import com.gallatinsystems.framework.rest.RestResponse;

public class DeviceNotificationResponse extends RestResponse {

	private static final long serialVersionUID = -5674899264132126425L;

	List<String> missingFiles = new ArrayList<String>();
	List<String> missingFilesUnknown = new ArrayList<String>();

	public void setMissingFiles(List<DeviceFileJobQueue> byDevice) {
		missingFiles = new ArrayList<String>();
		for (DeviceFileJobQueue df : byDevice) {
			missingFiles.add(df.getFileName());
		}
	}

	public void setMissingUnknown(List<DeviceFileJobQueue> unknown) {
		missingFilesUnknown = new ArrayList<String>();
		for (DeviceFileJobQueue df : unknown) {
			missingFilesUnknown.add(df.getFileName());
		}
	}

	public List<String> getMissingFiles() {
		return missingFiles;
	}

	public List<String> getMissingUnknown() {
		return missingFilesUnknown;
	}

}
