package com.gallatinsystems.flow.foundry.dao;

import com.gallatinsystems.flow.foundry.domain.FlowInstance;
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
