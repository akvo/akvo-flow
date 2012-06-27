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

package org.waterforpeople.mapping.app.gwt.client.diagnostics;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RemoteExceptionServiceAsync {

	void listRemoteExceptions(String phoneNumber, String deviceId,
			boolean unAckOnly, String cursor,
			AsyncCallback<ResponseDto<ArrayList<RemoteStacktraceDto>>> callback);

	void deleteRemoteStacktrace(Long exceptionId,
			AsyncCallback<Boolean> callback);

	void acknowledgeRemoteStacktrace(Long exceptionId,
			AsyncCallback<Boolean> callback);

	void deleteOldExceptions(AsyncCallback<Void> callback);

}
