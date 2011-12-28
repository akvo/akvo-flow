package org.waterforpeople.mapping.app.web.test;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.analytics.AccessPointMetricSummarizer;
import org.waterforpeople.mapping.analytics.dao.AccessPointMetricSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;

public class AccessPointMetricSummaryTest {

	public void runTest2(HttpServletResponse resp) {
		prepData(resp);
		AccessPointDao apDao = new AccessPointDao();
		List<AccessPoint> apList = apDao.listAccessPointBySubLevel(1,
				"Southern", null);
		AccessPointMetricSummaryDao apmsDao = new AccessPointMetricSummaryDao();
		for (AccessPoint ap : apList) {
			AccessPointMetricSummary prototype = new AccessPointMetricSummary();
			prototype.setCountry(ap.getCountryCode());
			prototype.setMetricGroup("TechnologyType");
			prototype.setMetricValue(ap.getTypeTechnologyString());
			prototype.setSubLevel(1);
			prototype.setSubValue(ap.getSub1());
			List<AccessPointMetricSummary> apmsList = apmsDao
					.listMetrics(prototype);
			if (apmsList == null || apmsList.isEmpty()) {
				prototype.setCount(1L);
				apmsDao.save(prototype);
				System.out.println("Adding new prototype metric "
						+ prototype.toString());
			} else {
				for (AccessPointMetricSummary apmsItem : apmsList) {
					Long oldCount = apmsItem.getCount();
					Long newCount = oldCount + 1L;
					apmsItem.setCount(newCount);
					apmsDao.save(apmsItem);
					System.out.println("Incrementing count of prototype"
							+ apmsItem.toString() + " from " + oldCount
							+ " to " + newCount);
				}
			}
		}

	}

	public void runTest(HttpServletResponse resp) {

		AccessPointMetricSummarizer apms = new AccessPointMetricSummarizer();

		AccessPointDao apDao = new AccessPointDao();
		for (AccessPoint ap : apDao.list("all")) {

			apms.performSummarization(String.valueOf(ap.getKey().getId()),
					null, null, null, null);
		}
	}

	private void clearAPMSTable() {
		AccessPointMetricSummaryDao apmsDao = new AccessPointMetricSummaryDao();
		for (AccessPointMetricSummary item : apmsDao.list("all")) {
			apmsDao.delete(item);
		}
	}

	private void prepData(HttpServletResponse resp) {
		AccessPointDao apDao = new AccessPointDao();
		for (AccessPoint item : apDao.listAccessPointByLocation("MW", null,
				null, null, "all")) {
			apDao.delete(item);
		}

		AccessPointTest aptest = new AccessPointTest();
		aptest.loadLots(resp, 70);
		clearAPMSTable();

	}
}
