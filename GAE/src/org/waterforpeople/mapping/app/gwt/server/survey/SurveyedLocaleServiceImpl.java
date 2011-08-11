package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyalValueDto;
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
	 * lists all the surveyalValue objects for a single surveyinstance
	 * 
	 * @param surveyInstanceId
	 * @return
	 */
	@Override
	public List<SurveyalValueDto> listSurveyalValuesByInstance(
			Long surveyInstanceId) {
		List<SurveyalValue> valList = localeDao
				.listSurveyalValuesByInstance(surveyInstanceId);
		List<SurveyalValueDto> dtoList = new ArrayList<SurveyalValueDto>();
		if (valList != null) {
			for (SurveyalValue val : valList) {
				SurveyalValueDto dto = new SurveyalValueDto();
				DtoMarshaller.copyToDto(val, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
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
				for (SurveyalValue v : vals) {
					SurveyedLocale l = localeDao.getByKey(v
							.getSurveyedLocaleId());
					// have to check for null since delete can produce orphans
					if (l != null) {
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

	/**
	 * saves a surveyedLocale to the database, including any nested
	 * surveyalValues
	 * 
	 * @param locale
	 * @return
	 */
	public SurveyedLocaleDto saveSurveyedLocale(SurveyedLocaleDto locale) {
		if (locale != null) {
			SurveyedLocale localeDomain = new SurveyedLocale();
			DtoMarshaller.copyToCanonical(localeDomain, locale);
			localeDao.save(localeDomain);
			if (locale.getValues() != null) {
				List<SurveyalValue> valueList = new ArrayList<SurveyalValue>();
				for (SurveyalValueDto val : locale.getValues()) {
					SurveyalValue valDomain = new SurveyalValue();
					DtoMarshaller.copyToCanonical(valDomain, val);
					valDomain
							.setSurveyedLocaleId(localeDomain.getKey().getId());
					if (valDomain.getCountryCode() == null) {
						valDomain.setCountryCode(localeDomain.getCountryCode());
					}
					if (valDomain.getSublevel1() == null) {
						valDomain.setSublevel1(localeDomain.getSublevel1());
					}
					if (valDomain.getSublevel2() == null) {
						valDomain.setSublevel2(localeDomain.getSublevel2());
					}
					if (valDomain.getSublevel3() == null) {
						valDomain.setSublevel3(localeDomain.getSublevel3());
					}
					if (valDomain.getSublevel4() == null) {
						valDomain.setSublevel4(localeDomain.getSublevel4());
					}
					if (valDomain.getSublevel5() == null) {
						valDomain.setSublevel5(localeDomain.getSublevel5());
					}
					if (valDomain.getSublevel6() == null) {
						valDomain.setSublevel6(localeDomain.getSublevel6());
					}
					if (valDomain.getCollectionDate() == null) {
						valDomain.setCollectionDate(localeDomain
								.getLastSurveyedDate());
						if (valDomain.getCollectionDate() == null) {
							valDomain.setCollectionDate(new Date());
						}
					}
					Calendar cal = new GregorianCalendar();
					cal.setTime(valDomain.getCollectionDate());
					valDomain.setDay(cal.get(Calendar.DAY_OF_MONTH));
					valDomain.setMonth(cal.get(Calendar.MONTH) + 1);
					valDomain.setYear(cal.get(Calendar.YEAR));
					if (valDomain.getLocaleType() == null) {
						valDomain.setLocaleType(localeDomain.getLocaleType());
					}
					if (valDomain.getOrganization() == null) {
						valDomain.setOrganization(localeDomain
								.getOrganization());
					}
					if (valDomain.getSystemIdentifier() != null) {
						valDomain.setSystemIdentifier(localeDomain
								.getSystemIdentifier());
					}
					valueList.add(valDomain);
				}
				if (valueList.size() > 0) {
					localeDao.save(valueList);
				}
			}
			// populate the key so the UI can see it
			locale.setKeyId(localeDomain.getKey().getId());
		}
		return locale;
	}
}
