package com.gallatinsystems.flowfoundry.client;

import java.util.List;

import com.gallatinsystems.flowfoundry.shared.dto.FlowInstanceDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FlowInstanceServiceAsync {

	void listInstances(String cursor, AsyncCallback<List<FlowInstanceDto>> callback);

	void saveInstance(FlowInstanceDto instance,
			AsyncCallback<FlowInstanceDto> callback);

}
