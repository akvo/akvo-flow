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
