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

}
