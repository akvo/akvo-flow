package com.gallatinsystems.flowfoundry.server;

import com.gallatinsystems.flowfoundry.server.domain.FlowInstance;
import com.gallatinsystems.framework.dao.BaseDAO;

/**
 * Handles persistence for FlowInstance objects
 *
 * @author Christopher Fagiani
 *
 */
public class FlowInstanceDao extends BaseDAO<FlowInstance> {

	public FlowInstanceDao() {
		super(FlowInstance.class);
	}

}
