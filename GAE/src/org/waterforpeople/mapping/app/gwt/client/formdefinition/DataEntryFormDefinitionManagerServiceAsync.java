package org.waterforpeople.mapping.app.gwt.client.formdefinition;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataEntryFormDefinitionManagerServiceAsync {

	void delete(DataEntryFormDefinitionDto item, AsyncCallback<Void> callback);

	void getByName(String name,
			AsyncCallback<DataEntryFormDefinitionDto> callback);

	void list(AsyncCallback<ArrayList<DataEntryFormDefinitionDto>> callback);

	void save(DataEntryFormDefinitionDto item,
			AsyncCallback<DataEntryFormDefinitionDto> callback);

}
