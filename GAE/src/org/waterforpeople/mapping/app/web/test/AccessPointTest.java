package org.waterforpeople.mapping.app.web.test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.analytics.MapSummarizer;
import org.waterforpeople.mapping.app.web.TestHarnessServlet;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.Status;

import com.gallatinsystems.gis.map.dao.MapFragmentDao;

public class AccessPointTest {
	private static Logger log = Logger.getLogger(TestHarnessServlet.class
			.getName());
	public void loadLots(HttpServletResponse resp){
		MapFragmentDao mfDao = new MapFragmentDao();
		AccessPointDao apDao = new AccessPointDao();
		for (int j = 0; j < 1; j++) {
			double lat = -15 + (new Random().nextDouble() / 10);
			double lon = 35 + (new Random().nextDouble() / 10);
			for (int i = 0; i < 50; i++) {
				AccessPoint ap = new AccessPoint();
				ap.setLatitude(lat);
				ap.setLongitude(lon);
				Calendar calendar = Calendar.getInstance();
				Date today = new Date();
				calendar.setTime(today);
				calendar.add(Calendar.YEAR, -1 * i);
				System.out
						.println("AP: " + ap.getLatitude() + "/"
								+ ap.getLongitude() + "Date: "
								+ calendar.getTime());
				// ap.setCollectionDate(calendar.getTime());
				ap.setAltitude(0.0);
				ap.setCommunityCode("test" + new Date());
				ap.setCommunityName("test" + new Date());
				ap.setPhotoURL("http://waterforpeople.s3.amazonaws.com/images/peru/pc28water.jpg");
				ap.setProvideAdequateQuantity(true);
				ap.setHasSystemBeenDown1DayFlag(false);
				ap.setMeetGovtQualityStandardFlag(true);
				ap.setMeetGovtQuantityStandardFlag(false);
				ap.setCurrentManagementStructurePoint("Community Board");
				ap.setDescription("Waterpoint");
				ap.setDistrict("test district");
				ap.setEstimatedHouseholds(100L);
				ap.setEstimatedPeoplePerHouse(11L);
				ap.setFarthestHouseholdfromPoint("Yes");
				ap.setNumberOfHouseholdsUsingPoint(100L);
				ap.setConstructionDateYear("2001");
				ap.setCostPer(1.0);
				ap.setCountryCode("MW");
				ap.setConstructionDate(new Date());
				ap.setCollectionDate(new Date());
				ap.setPhotoName("Water point");
				if (i % 2 == 0)
					ap.setPointType(AccessPoint.AccessPointType.WATER_POINT);
				else if (i % 3 == 0)
					ap.setPointType(AccessPoint.AccessPointType.SANITATION_POINT);
				else
					ap.setPointType(AccessPoint.AccessPointType.PUBLIC_INSTITUTION);
				if (i == 0)
					ap.setPointStatus(AccessPoint.Status.FUNCTIONING_HIGH);
				else if (i == 1)
					ap.setPointStatus(AccessPoint.Status.FUNCTIONING_OK);
				else if (i == 2)
					ap.setPointStatus(Status.FUNCTIONING_WITH_PROBLEMS);
				else
					ap.setPointStatus(Status.NO_IMPROVED_SYSTEM);

				if (i % 2 == 0)
					ap.setTypeTechnologyString("Kiosk");
				else
					ap.setTypeTechnologyString("Afridev Handpump");
				apDao.save(ap);
				MapSummarizer ms = new MapSummarizer();
				// ms.performSummarization("" + ap.getKey().getId(), "");
				if (i % 50 == 0)
					log.log(Level.INFO, "Loaded to " + i);
			}
		}
		try {
			resp.getWriter().println("Finished loading aps");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
