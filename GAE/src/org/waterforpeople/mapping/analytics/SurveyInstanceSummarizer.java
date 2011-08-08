package org.waterforpeople.mapping.analytics;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.GeoCoordinates;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.gis.location.GeoLocationService;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;

/**
 * Populates SurveyInstanceSummary objects (a roll-up that aggregates survey
 * instances by country/region/day)
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyInstanceSummarizer implements DataSummarizer {

	private static Logger logger = Logger
			.getLogger(SurveyInstanceSummarizer.class.getName());

	private static final String GEO_TYPE = "GEO";
	private SurveyInstanceDAO instanceDao;

	public SurveyInstanceSummarizer() {
		instanceDao = new SurveyInstanceDAO();
	}

	/**
	 * looks up a survey instance then finds it's corresponding country and (if
	 * possible) sublevel1 using the GIS serviceF
	 */
	@Override
	public boolean performSummarization(String key, String type, String value,
			Integer offset, String cursor) {
		boolean success = false;
		if (key != null) {
			SurveyInstance instance = instanceDao.getByKey(new Long(key));
			List<QuestionAnswerStore> qasList = instanceDao
					.listQuestionAnswerStoreByType(new Long(key), GEO_TYPE);
			if (qasList != null) {
				GeoCoordinates geoC = null;
				for (QuestionAnswerStore q : qasList) {
					if (q.getValue() != null && q.getValue().trim().length()>0) {
						geoC = GeoCoordinates
								.extractGeoCoordinate(q.getValue());
						if (geoC != null) {
							break;
						}
					}
				}
				if (geoC != null) {
					GeoLocationService gisService = new GeoLocationServiceGeonamesImpl();
					GeoPlace gp = gisService.findDetailedGeoPlace(geoC
							.getLatitude().toString(), geoC.getLongitude()
							.toString());
					if (gp != null) {
						SurveyInstanceSummaryDao.incrementCount(gp.getSub1(),
								gp.getCountryCode(),
								instance.getCollectionDate());
						success = true;
					}
				}
			}
			if (!success) {
				logger.log(
						Level.SEVERE,
						"Couldn't find community for instance. Was the community saved correctly? Instance id: "
								+ key);
			}
		}

		return true;
	}

	@Override
	public String getCursor() {
		return null;
	}
}