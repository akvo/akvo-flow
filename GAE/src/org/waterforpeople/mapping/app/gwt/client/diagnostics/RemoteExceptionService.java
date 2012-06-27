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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for browsing/deleting remote exceptions
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("remoteexceptionrpcservice")
public interface RemoteExceptionService extends RemoteService {
	public ResponseDto<ArrayList<RemoteStacktraceDto>> listRemoteExceptions(
			String phoneNumber, String deviceId, boolean unAckOnly,
			String cursor);

	/**
	 * deletes the single RemoteStacktrace object identified by the key id
	 * passed in
	 * 
	 * @param exceptionId
	 * @return
	 */
	public boolean deleteRemoteStacktrace(Long exceptionId);

	/**
	 * acknowledges a RemoteStacktrace by setting the acknowledged flag to true.
	 * This will prevent it from showing up in the UI but will keep it in the
	 * data store.
	 * 
	 * @param exceptionId
	 * @return
	 */
	public boolean acknowledgeRemoteStacktrace(Long exceptionId);

	/**
	 * deletes all items older than 30 days
	 */
	public void deleteOldExceptions();

}
