package org.waterforpeople.mapping.app.gwt.client.standardscoring;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("standardscoring")
public interface StandardScoringManagerService extends RemoteService {
	ResponseDto<ArrayList<StandardScoringDto>> listStandardScoring(
			Long scoreBucketKey, String cursorString);
	StandardScoringDto save(StandardScoringDto item);
	void delete(Long id);
	ArrayList<StandardScoreBucketDto> listStandardScoreBuckets();
	TreeMap<String, String> listObjectAttributes(String objectName);
	StandardScoreBucketDto save(StandardScoreBucketDto item);
	ArrayList<StandardContainerDto> listStandardContainer(String standardType);
}
