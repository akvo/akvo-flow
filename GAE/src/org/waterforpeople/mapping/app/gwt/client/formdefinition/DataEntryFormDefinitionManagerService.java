package org.waterforpeople.mapping.app.gwt.client.formdefinition;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;

public interface DataEntryFormDefinitionManagerService extends RemoteService{
	DataEntryFormDefinitionDto save(DataEntryFormDefinitionDto item);
	DataEntryFormDefinitionDto getByName(String name);
	void delete(DataEntryFormDefinitionDto item);
	ArrayList<DataEntryFormDefinitionDto> list();
}
