package org.waterforpeople.mapping.app.gwt.client.standardscoring;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StandardScoringManagerServiceAsync {

	void delete(Long id, AsyncCallback<Void> callback);

	void listStandardScoring(String cursorString,
			AsyncCallback<ResponseDto<ArrayList<StandardScoringDto>>> callback);

	void save(StandardScoringDto item,
			AsyncCallback<StandardScoringDto> callback);

}
