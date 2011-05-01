package com.gallatinsystems.flow.foundry.client;

import java.util.List;

import com.gallatinsystems.flow.foundry.domain.FlowInstance;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FlowInstanceServiceAsync {
	public void listInstances(String cursor,
			AsyncCallback<List<FlowInstance>> callback);

	public void saveInstance(FlowInstance instance,
			AsyncCallback<FlowInstance> callback);
}
