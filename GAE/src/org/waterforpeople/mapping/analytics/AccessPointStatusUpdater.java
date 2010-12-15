package org.waterforpeople.mapping.analytics;

import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.AccessPointStatusSummaryDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;

import com.gallatinsystems.common.util.DateUtil;
import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.domain.DataChangeRecord;

/**
 * handles changes to access point status values
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointStatusUpdater implements DataSummarizer {
	
	private static Logger logger = Logger.getLogger(AccessPointUpdater.class
			.getName());

	@Override
	public String getCursor() {
		// no-op
		return null;
	}

	@Override
	public boolean performSummarization(String key, String type, String value,
			Integer offset, String cursor) {
		DataChangeRecord change = new DataChangeRecord(value);
		AccessPoint oldPoint = hydratePoint(change.getOldVal());
		AccessPoint newPoint = hydratePoint(change.getNewVal());
		AccessPointStatusSummaryDao.incrementCount(oldPoint, null, -1);
		AccessPointStatusSummaryDao.incrementCount(newPoint, null, 1);
		return true;
	}

	/**
	 * uses the packed string passed in to initialize the values of an
	 * AccessPoint object.
	 * 
	 * @param vals
	 * @return
	 */
	private AccessPoint hydratePoint(String packedString) {
		String[] vals = packedString.split("\\|");
		AccessPoint point = null;
		if (vals.length == 5) {
			point = new AccessPoint();
			point.setCountryCode(vals[0]);
			point.setCommunityCode(vals[1]);
			try{
				point.setPointType(AccessPointType.valueOf(vals[2]));
			}catch(Throwable e){
				logger.warning("unknown type value: "+vals[2]);
			}
			try{
				point.setPointStatus(Status.valueOf(vals[3]));
			}catch(Throwable e){
				logger.warning("unknown status value: "+vals[3]);
			}
			try{
			point.setCollectionDate(DateUtil.getYearOnlyDate(vals[4]));
			}catch(Throwable e){
				logger.warning("bad date value: "+vals[4]);
			}
		}
		return point;
	}
}
