package org.waterforpeople.mapping.app.gwt.client.displaytemplate;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;

public interface DisplayTemplateManagerService extends RemoteService {
	ArrayList<String> getLabels();
	ArrayList<DisplayTemplateMappingDto> getRows();
	DisplayTemplateMappingDto save(DisplayTemplateMappingDto item);
	void delete(Long keyId);
	ArrayList<String> listObjectAttributes(String objectNames);
	
}
