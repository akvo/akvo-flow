package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.MetricDto;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.metric.dao.MetricDao;
import com.gallatinsystems.metric.domain.Metric;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MetricServiceImpl extends RemoteServiceServlet implements
		MetricService {

	private static final long serialVersionUID = -7385390184438218799L;
	private MetricDao metricDao;

	public MetricServiceImpl() {
		metricDao = new MetricDao();
	}

	/**
	 * lists all metrics, optionally filtered by organization, valueType and
	 * name and group
	 * 
	 * 
	 * @param organizationName
	 * @return
	 */
	@Override
	public ResponseDto<ArrayList<MetricDto>> listMetrics(String name,
			String group, String valueType, String organizationName,
			String cursor) {
		List<Metric> metrics = metricDao.listMetrics(name, group, valueType,
				organizationName, cursor);
		ResponseDto<ArrayList<MetricDto>> resp = new ResponseDto<ArrayList<MetricDto>>();
		ArrayList<MetricDto> dtoList = new ArrayList<MetricDto>();
		if (metrics != null) {
			for (Metric m : metrics) {
				MetricDto dto = new MetricDto();
				DtoMarshaller.copyToDto(m, dto);
				dtoList.add(dto);
			}
			resp.setCursorString(MetricDao.getCursor(metrics));
		}
		resp.setPayload(dtoList);
		return resp;
	}

	
	/**
	 * deletes the metric with the given id. this method does NOT remove orphans
	 */
	@Override
	public void deleteMetric(Long id) {
		Metric m = metricDao.getByKey(id);
		if(m!=null){
			metricDao.delete(m);
		}
	}

}
