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

package org.waterforpeople.mapping.app.gwt.server.diagnostics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.diagnostics.RemoteExceptionService;
import org.waterforpeople.mapping.app.gwt.client.diagnostics.RemoteStacktraceDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.diagnostics.dao.RemoteStacktraceDao;
import com.gallatinsystems.diagnostics.domain.RemoteStacktrace;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * service for browsing/deleting remote exceptions
 * 
 * @author Christopher Fagiani
 * 
 */
public class RemoteExceptionServiceImpl extends RemoteServiceServlet implements
		RemoteExceptionService {

	private static final long serialVersionUID = -4298841746085332519L;
	private RemoteStacktraceDao traceDao;

	public RemoteExceptionServiceImpl() {
		traceDao = new RemoteStacktraceDao();
	}

	/**
	 * searches for remoteStacktrace objects that match the parameters passed
	 * in.
	 */
	public ResponseDto<ArrayList<RemoteStacktraceDto>> listRemoteExceptions(
			String phoneNumber, String deviceId, boolean unAckOnly,
			String cursor) {
		ResponseDto<ArrayList<RemoteStacktraceDto>> responseDto = new ResponseDto<ArrayList<RemoteStacktraceDto>>();
		List<RemoteStacktrace> traceList = traceDao.listStacktrace(phoneNumber,
				deviceId, unAckOnly, cursor);
		ArrayList<RemoteStacktraceDto> dtoList = new ArrayList<RemoteStacktraceDto>();
		if (traceList != null) {
			for (RemoteStacktrace trace : traceList) {
				RemoteStacktraceDto dto = new RemoteStacktraceDto();
				DtoMarshaller.copyToDto(trace, dto);
				if (trace.getStackTrace() != null) {
					dto.setStackTrace(trace.getStackTrace().getValue());
				}
				dtoList.add(dto);
			}
			responseDto.setCursorString(RemoteStacktraceDao
					.getCursor(traceList));
			responseDto.setPayload(dtoList);
		}
		return responseDto;
	}

	/**
	 * deletes the single RemoteStacktrace object identified by the key id
	 * passed in
	 * 
	 * @param exceptionId
	 * @return
	 */
	public boolean deleteRemoteStacktrace(Long exceptionId) {
		boolean deleted = false;
		if (exceptionId != null) {
			RemoteStacktrace trace = traceDao.getByKey(exceptionId);
			if (trace != null) {
				traceDao.delete(trace);
				deleted = true;
			}
		}
		return deleted;
	}

	/**
	 * acknowledges a RemoteStacktrace by setting the acknowledged flag to true.
	 * This will prevent it from showing up in the UI but will keep it in the
	 * data store.
	 * 
	 * @param exceptionId
	 * @return
	 */
	public boolean acknowledgeRemoteStacktrace(Long exceptionId) {
		boolean acked = false;
		if (exceptionId != null) {
			RemoteStacktrace trace = traceDao.getByKey(exceptionId);
			if (trace != null) {
				trace.setAcknowleged(true);
				traceDao.save(trace);
				acked = true;
			}
		}
		return acked;
	}

	/**
	 * deletes exceptions older than 30 days
	 */
	public void deleteOldExceptions() {
		Calendar cal = new GregorianCalendar();
		cal.roll(Calendar.MONTH, false);
		traceDao.deleteItemsOlderThan(cal.getTime());
	}

}
