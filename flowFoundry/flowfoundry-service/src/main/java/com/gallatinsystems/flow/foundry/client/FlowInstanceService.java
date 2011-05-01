package com.gallatinsystems.flow.foundry.client;

import java.util.List;

import com.gallatinsystems.flow.foundry.domain.FlowInstance;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("flowinstancerpcservice")
public interface FlowInstanceService extends RemoteService {

	public List<FlowInstance> listInstances(String cursor);
	public FlowInstance saveInstance(FlowInstance instance);
	
}
