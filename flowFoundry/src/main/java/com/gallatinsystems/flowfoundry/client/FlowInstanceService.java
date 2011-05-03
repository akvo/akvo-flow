package com.gallatinsystems.flowfoundry.client;

import java.util.List;

import com.gallatinsystems.flowfoundry.shared.dto.FlowInstanceDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("flowinstancerpcservice")
public interface FlowInstanceService extends RemoteService {

	public List<FlowInstanceDto> listInstances(String cursor);
	public FlowInstanceDto saveInstance(FlowInstanceDto instance);	
	
}
