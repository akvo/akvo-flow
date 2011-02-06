package org.waterforpeople.mapping.app.gwt.client.displaytemplate;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DisplayTemplateManagerServiceAsync {

	void getLabels(AsyncCallback<ArrayList<String>> callback);

	void getRows(AsyncCallback<ArrayList<MapBalloonDefinitionDto>> callback);

	void delete(Long keyId, AsyncCallback<Void> callback);

	void listObjectAttributes(String objectNames,
			AsyncCallback<ArrayList<String>> callback);

	void save(MapBalloonDefinitionDto item,
			AsyncCallback<MapBalloonDefinitionDto> callback);

}
