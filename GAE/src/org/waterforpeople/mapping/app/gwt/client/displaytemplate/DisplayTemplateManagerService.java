package org.waterforpeople.mapping.app.gwt.client.displaytemplate;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;

public interface DisplayTemplateManagerService extends RemoteService {
	ArrayList<String> getLabels();
	ArrayList<MapBalloonDefinitionDto> getRows();
	MapBalloonDefinitionDto save(MapBalloonDefinitionDto item);
	void delete(Long keyId);
	ArrayList<String> listObjectAttributes(String objectNames);
	
}
