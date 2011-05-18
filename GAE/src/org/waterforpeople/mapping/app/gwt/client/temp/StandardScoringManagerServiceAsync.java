package org.waterforpeople.mapping.app.gwt.client.temp;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StandardScoringManagerServiceAsync {

	void delete(Long id, AsyncCallback<Void> callback);

	void listStandardScoring(String cursorString,
			AsyncCallback<List<StandardScoringDto>> callback);

	void save(StandardScoringDto item,
			AsyncCallback<StandardScoringDto> callback);

}
