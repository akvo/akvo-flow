package org.waterforpeople.mapping.app.web.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.web.TestHarnessServlet;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.gis.map.dao.MapFragmentDao;

public class AccessPointTest {
	private static Logger log = Logger.getLogger(TestHarnessServlet.class
			.getName());

	public void loadLots(HttpServletResponse resp) {
		try {
			MapFragmentDao mfDao = new MapFragmentDao();
			AccessPointDao apDao = new AccessPointDao();
			AccessPointHelper aph = new AccessPointHelper();

			ArrayList<AccessPoint> apList = new ArrayList<AccessPoint>();
			for (int j = 0; j < 1; j++) {

				for (int i = 0; i < 700; i++) {
					// double lon = 35 + (new Random().nextDouble() / new
					// Random().nextInt(10));
					// double lat = -15 + (new Random().nextDouble() / new
					// Random().nextInt(10));
					
					//ghana 7.9596438809,-1.20704621427
//					double lon = -106;
//					double lat = 39.1;
					double lon = -1.1;
					double lat = 7.0;
					if (getRandomBoolean()) {
						lon = lon
								+ (new Random().nextDouble() * new Random()
										.nextInt(10));
					} else {
						lon = lon
								- (new Random().nextDouble() * new Random()
										.nextInt(10));

					}
					
					if (getRandomBoolean()) {
						lat = lat
								+ (new Random().nextDouble() * new Random()
										.nextInt(10));
					}else{
						lat = lat - (new Random().nextDouble() * new Random()
						.nextInt(10));
					}
					Calendar calendar = new GregorianCalendar(2010,
							Calendar.JANUARY, 1);
					Integer sign = null;
					if (new Random().nextInt(3) % 2 == 0) {
						sign = -1;
					} else {
						sign = 1;
					}
					calendar.add(Calendar.MONTH,
							sign * new Random().nextInt(100));
					log.info(i + ":");
					AccessPoint ap = new AccessPoint();
					ap.setLatitude(lat);
					ap.setLongitude(lon);
					ap.setCountryCode("GH");
					Date today = new Date();
					System.out
							.println("AP: " + ap.getLatitude() + "/"
									+ ap.getLongitude() + "Date: "
									+ calendar.getTime());
					// ap.setCollectionDate(calendar.getTime());
					ap.setAltitude(0.0);
					ap.setCommunityCode("test" + new Date());
					ap.setCommunityName("test" + new Date());
					ap.setPhotoURL("http://waterforpeople.s3.amazonaws.com/images/peru/pc28water.jpg");
					ap.setImprovedWaterPointFlag(getRandomBoolean());
					ap.setProvideAdequateQuantity(getRandomBoolean());
					ap.setHasSystemBeenDown1DayFlag(getRandomBoolean());
					ap.setMeetGovtQualityStandardFlag(getRandomBoolean());
					ap.setMeetGovtQuantityStandardFlag(getRandomBoolean());
					ap.setWaterForPeopleProjectFlag(getRandomBoolean());
					ap.setWaterAvailableDayVisitFlag(getRandomBoolean());
					ap.setEstimatedPeoplePerHouse(new Random().nextLong());
					ap.setCollectTariffFlag(getRandomBoolean());
					ap.setCurrentManagementStructurePoint("Community Board");
					ap.setDescription("Waterpoint");
					ap.setDistrict("test district");
					ap.setEstimatedHouseholds(new Random().nextLong());
					ap.setFarthestHouseholdfromPoint("Yes");
					ap.setNumberOfHouseholdsUsingPoint(new Random().nextLong());
					Integer year = new Random().nextInt(2011);
					ap.setConstructionDateYear(year.toString());
					ap.setCostPer(1.0);
					ap.setCollectionDate(calendar.getTime());
					calendar.add(Calendar.YEAR, -5);
					ap.setConstructionDate(calendar.getTime());
					ap.setPhotoName("Water point");

					if (getRandomBoolean())
						ap.setCurrentProblem("Yes");

					if (getRandomBoolean())
						ap.setPointType(AccessPoint.AccessPointType.WATER_POINT);
					else
						ap.setPointType(AccessPoint.AccessPointType.PUBLIC_INSTITUTION);
					if (getRandomBoolean())
						ap.setTypeTechnologyString("Kiosk");
					else
						ap.setTypeTechnologyString("Afridev Handpump");

					apList.add(ap);
					if (i % 50 == 0)
						log.log(Level.INFO, "Loaded to " + i);
					aph.saveAccessPoint(ap);
				}
				resp.getWriter().println("About to save APs");

				// apDao.save(apList);
				resp.getWriter().println("Finished saving APs");

				// for(AccessPoint ap :apList){
				// CommunityLocationSummarizer cls = new
				// CommunityLocationSummarizer();
				// cls.performSummarization(String.valueOf(ap.getKey().getId()),
				// null, null, null, null);
				// MapSummarizer ms = new MapSummarizer();
				// }
			}
			resp.getWriter().println("Finished loading APs");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Boolean getRandomBoolean() {
		Integer seed = new Random().nextInt(2);
		if (seed == 0) {
			return false;
		} else {
			return true;
		}
	}
}
