package com.gallatinsystems.flow.foundry.app.gwt.server;

import java.util.List;

import com.gallatinsystems.flow.foundry.dao.FlowInstanceDao;
import com.gallatinsystems.flow.foundry.domain.FlowInstance;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service to handle requests for Flow Instances
 * 
 * @author Christopher Fagiani
 * 
 */
public class FlowInstanceServiceImpl extends RemoteServiceServlet {

	private static final long serialVersionUID = 7354883264975004173L;
	private FlowInstanceDao flowInstanceDao;
	
	public FlowInstanceServiceImpl(){
		flowInstanceDao = new FlowInstanceDao();
	}
	
	public List<FlowInstance> listInstances(String cursor){
		return flowInstanceDao.list(cursor);
	}
	
	public FlowInstance saveInstance(FlowInstance instance){
		return flowInstanceDao.saveOrUpdate(instance);
	}

}
