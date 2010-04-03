package org.waterforpeople.mapping.app.gwt.server.accesspoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.AccessPointStatusSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointStatusSummary;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryService;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * This service is responsible for returning access point summarization objects. The
 * fields within the summary DTO may be partially populated based on the type of
 * summarization being returned.
 *
 * @author Christopher Fagiani
 *
 */
public class AccessPointSummaryServiceImpl extends RemoteServiceServlet implements
		AccessPointSummaryService {

	private static final long serialVersionUID = -5722103696712574220L;
	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(DeviceManagerServlet.class.getName());

	/**
	 * returns an array of AccessPointSummaryDto objects that match the criteria
	 * passed in. The summary objects will have the status and count populated.
	 */
	public AccessPointSummaryDto[] listAccessPointStatusSummary(String country, String community, String type, String year) {
		AccessPointStatusSummaryDao summaryDao = new AccessPointStatusSummaryDao();
		List<AccessPointStatusSummary> summaries = summaryDao
				.listByLocationAndYear(country,community,type,year);
		AccessPointSummaryDto[] dtoList = null;
		if (summaries != null) {
			//first, collapse based on status
			Map<String,Long> statusCount = new HashMap<String,Long>();


			for (int i = 0; i < summaries.size(); i++) {
				Long curVal = statusCount.get(summaries.get(i).getStatus());
				if(curVal == null){
					curVal = summaries.get(i).getCount();
				}else{
					curVal = curVal + summaries.get(i).getCount();
				}
				statusCount.put(summaries.get(i).getStatus(),curVal);
			}
			dtoList = new AccessPointSummaryDto[statusCount.keySet().size()];
			int i = 0;
			for(String key: statusCount.keySet()){
				AccessPointSummaryDto dto = new AccessPointSummaryDto();
				dto.setCountryCode(country);
				dto.setCommunityCode(community);
				dto.setType(type);
				dto.setYear(year);
				dto.setStatus(key);
				dto.setCount(statusCount.get(key));
				dtoList[i++] = dto;
			}
		}
		return dtoList;
	}
}