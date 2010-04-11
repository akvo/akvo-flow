package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;

public interface TechnologyTypeService extends RemoteService {
	ArrayList<TechnologyTypeDto> list();
	TechnologyTypeDto save(TechnologyTypeDto techTypeDto);
	void delete(Long id);
	TechnologyTypeDto get(Long id);

}
