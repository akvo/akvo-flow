package org.waterforpeople.mapping.app.web.test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.analytics.CommunityLocationSummarizer;
import org.waterforpeople.mapping.analytics.MapSummarizer;
import org.waterforpeople.mapping.app.web.TestHarnessServlet;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.Status;

import com.gallatinsystems.gis.map.dao.MapFragmentDao;

public class AccessPointTest {
	private static Logger log = Logger.getLogger(TestHarnessServlet.class
			.getName());

	public void loadLots(HttpServletResponse resp) {
		MapFragmentDao mfDao = new MapFragmentDao();
		AccessPointDao apDao = new AccessPointDao();
		for (int j = 0; j < 1; j++) {
			double lat = 6.6 + (new Random().nextDouble() / 10);
			double lon = -10.8 + (new Random().nextDouble() / 10);
			for (int i = 0; i < 700; i++) {
				Calendar calendar = new GregorianCalendar(2010,
						Calendar.JANUARY, 1);
				Integer sign = null;
				if (new Random().nextInt(2) % 2 == 0) {
					sign = -1;
				} else {
					sign = 1;
				}
				calendar.add(Calendar.MONTH, sign * new Random().nextInt(100));
				log.info(i + ":");
				AccessPoint ap = new AccessPoint();
				ap.setLatitude(lat + new Random().nextInt(100) / 100);
				ap.setLongitude(lon + new Random().nextInt(100) / 100);

				Date today = new Date();
				System.out.println("AP: " + ap.getLatitude() + "/"
						+ ap.getLongitude() + "Date: " + calendar.getTime());
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
				ap.setCountryCode("LR");
				ap.setCollectionDate(calendar.getTime());
				calendar.add(Calendar.YEAR, -5);
				ap.setConstructionDate(calendar.getTime());
				ap.setPhotoName("Water point");
				if (i % 2 == 0)
					ap.setPointType(AccessPoint.AccessPointType.WATER_POINT);
				else
					ap.setPointType(AccessPoint.AccessPointType.PUBLIC_INSTITUTION);
				if (i % 2 == 0)
					ap.setTypeTechnologyString("Kiosk");
				else
					ap.setTypeTechnologyString("Afridev Handpump");
				apDao.save(ap);
				CommunityLocationSummarizer cls = new CommunityLocationSummarizer();
				cls.performSummarization(String.valueOf(ap.getKey().getId()),
						null, null, null, null);
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
