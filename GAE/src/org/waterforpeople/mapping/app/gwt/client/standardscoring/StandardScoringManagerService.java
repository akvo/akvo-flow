package org.waterforpeople.mapping.app.gwt.client.standardscoring;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface StandardScoringManagerService extends RemoteService {
	List<StandardScoringDto> listStandardScoring(String cursorString);
	StandardScoringDto save(StandardScoringDto item);
	void delete(Long id);
}
