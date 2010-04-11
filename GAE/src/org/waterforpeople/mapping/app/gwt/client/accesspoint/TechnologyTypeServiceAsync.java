package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TechnologyTypeServiceAsync {

	void delete(Long id, AsyncCallback<Void> callback);

	void get(Long id, AsyncCallback<TechnologyTypeDto> callback);

	void list(AsyncCallback<List<TechnologyTypeDto>> callback);

	void save(TechnologyTypeDto techTypeDto,
			AsyncCallback<TechnologyTypeDto> callback);

}
