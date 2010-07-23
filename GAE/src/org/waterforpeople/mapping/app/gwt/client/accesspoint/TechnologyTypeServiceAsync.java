package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TechnologyTypeServiceAsync {

	void delete(Long id, AsyncCallback<Void> callback);

	void get(Long id, AsyncCallback<TechnologyTypeDto> callback);

	void list(AsyncCallback<ArrayList<TechnologyTypeDto>> asyncCallback);

	void save(TechnologyTypeDto techTypeDto,
			AsyncCallback<TechnologyTypeDto> callback);

}
