package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface TechnologyTypeService extends RemoteService {
	List<TechnologyTypeDto> list();
	TechnologyTypeDto save(TechnologyTypeDto techTypeDto);
	void delete(Long id);
	TechnologyTypeDto get(Long id);

}
