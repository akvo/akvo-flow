package com.gallatinsystems.flowfoundry.server;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.flowfoundry.server.domain.FlowInstance;
import com.gallatinsystems.flowfoundry.shared.dto.FlowInstanceDto;
import com.gallatinsystems.util.DtoMarshaller;
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

	public FlowInstanceServiceImpl() {
		flowInstanceDao = new FlowInstanceDao();
	}

	public List<FlowInstanceDto> listInstances(String cursor) {
		List<FlowInstance> results = flowInstanceDao.list(cursor);
		List<FlowInstanceDto> dtoList = new ArrayList<FlowInstanceDto>();
		if (results != null) {
			for (FlowInstance i : results) {
				FlowInstanceDto dto = new FlowInstanceDto();
				DtoMarshaller.getInstance().copyToDto(i, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	public FlowInstanceDto saveInstance(FlowInstanceDto instance) {
		if (instance != null) {
			FlowInstance domain = new FlowInstance();
			DtoMarshaller.getInstance().copyToCanonical(domain, instance);
			domain = flowInstanceDao.saveOrUpdate(domain);
			instance.setKeyId(domain.getKey().getId());
		}
		return instance;

	}

}
