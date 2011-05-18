package org.waterforpeople.mapping.app.gwt.client.standardscoring;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("standardscoring")
public interface StandardScoringManagerService extends RemoteService {
	ResponseDto<ArrayList<StandardScoringDto>> listStandardScoring(
			String cursorString);
	StandardScoringDto save(StandardScoringDto item);
	void delete(Long id);
}
