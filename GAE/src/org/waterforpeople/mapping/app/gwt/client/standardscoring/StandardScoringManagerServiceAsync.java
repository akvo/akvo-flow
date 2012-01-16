package org.waterforpeople.mapping.app.gwt.client.standardscoring;

import java.util.ArrayList;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StandardScoringManagerServiceAsync {

	void delete(Long id, AsyncCallback<Void> callback);

	void listStandardScoring(Long scoreBucketKey, String cursorString,
			AsyncCallback<ResponseDto<ArrayList<StandardScoringDto>>> callback);

	void save(StandardScoringDto item,
			AsyncCallback<StandardScoringDto> callback);

	void listStandardScoreBuckets(
			AsyncCallback<ArrayList<StandardScoreBucketDto>> asyncCallback);

	void listObjectAttributes(String objectName,
			AsyncCallback<TreeMap<String, String>> callback);

	void save(StandardScoreBucketDto item,
			AsyncCallback<StandardScoreBucketDto> callback);

	void listStandardContainer(String standardType,
			AsyncCallback<ArrayList<StandardContainerDto>> callback);

	void saveCompoundRule(Long compoundRuleId, String standardType,
			Long leftRuleId, Long rightRuleId, String operator,
			AsyncCallback<Long> callback);

	void listCompoundRule(String standardType, AsyncCallback<Void> callback);

}
