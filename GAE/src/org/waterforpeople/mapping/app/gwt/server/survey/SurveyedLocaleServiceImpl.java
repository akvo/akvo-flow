package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * GWT-RPC service implementation for the surveyedLocale domain.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyedLocaleServiceImpl extends RemoteServiceServlet implements
		SurveyedLocaleService {
	private static final long serialVersionUID = 5175636222426573649L;
	private SurveyedLocaleDao localeDao;

	public SurveyedLocaleServiceImpl() {
		localeDao = new SurveyedLocaleDao();
	}

	/**
	 * deletes a single locale identified by the id passed in. This method does
	 * NOT delete any of the associated values nor any survey instances tied to
	 * this locale (thus it can create orphans).
	 */
	@Override
	public void deleteLocale(Long localeId) {
		SurveyedLocale l = localeDao.getByKey(localeId);
		if (l != null) {
			localeDao.delete(l);
		}
	}

	/**
	 * performs a search to find surveyedLocale records that match the critieria
	 * passed in
	 */
	@Override
	public ResponseDto<ArrayList<SurveyedLocaleDto>> listLocales(
			AccessPointSearchCriteriaDto searchCriteria, String cursorString) {
		List<SurveyedLocale> pointList = null;
		String cursorToReturn = null;
		if (searchCriteria.getMetricId() != null) {
			List<SurveyalValue> vals = localeDao.listSurveyalValueByMetric(
					Long.parseLong(searchCriteria.getMetricId()),
					searchCriteria.getMetricValue(),
					searchCriteria.getPageSize(), cursorString);
			if (vals != null) {
				// TODO: get items matching id in a more efficient manner
				pointList = new ArrayList<SurveyedLocale>();
				for(SurveyalValue v: vals){
					SurveyedLocale l = localeDao.getByKey(v.getSurveyedLocaleId());
					//have to check for null since delete can produce orphans
					if(l != null){
						pointList.add(l);
					}
				}
			}

		} else {
			pointList = localeDao.search(searchCriteria.getCountryCode(),
					searchCriteria.getCollectionDateFrom(),
					searchCriteria.getCollectionDateTo(),
					searchCriteria.getPointType(), searchCriteria.getOrderBy(),
					searchCriteria.getOrderByDir(),
					searchCriteria.getPageSize(), cursorString);
			cursorToReturn = SurveyedLocaleDao.getCursor(pointList);
		}
		ArrayList<SurveyedLocaleDto> dtoList = new ArrayList<SurveyedLocaleDto>();
		if (pointList != null) {
			for (SurveyedLocale item : pointList) {
				SurveyedLocaleDto dto = new SurveyedLocaleDto();
				DtoMarshaller.copyToDto(item, dto);
				dtoList.add(dto);
			}
		}

		ResponseDto<ArrayList<SurveyedLocaleDto>> result = new ResponseDto<ArrayList<SurveyedLocaleDto>>();
		result.setCursorString(cursorToReturn);
		result.setPayload(dtoList);

		return result;
	}

}
