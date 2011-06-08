package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MetricServiceAsync {

	void listMetrics(String name, String group, String valueType,
			String organizationName, String cursor,
			AsyncCallback<ResponseDto<ArrayList<MetricDto>>> callback);

	void deleteMetric(Long id, AsyncCallback<Void> callback);

}
