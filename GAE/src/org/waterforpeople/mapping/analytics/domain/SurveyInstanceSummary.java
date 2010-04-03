package org.waterforpeople.mapping.analytics.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * data structure for country/community/day roll-ups of survey instances
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class SurveyInstanceSummary extends BaseDomain {
	private static final long serialVersionUID = -1512678535036285469L;

	@Persistent
	private Date collectionDate;
	@Persistent
	private String countryCode;
	@Persistent
	private String communityCode;
	@Persistent
	private Long count;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCommunityCode() {
		return communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

}
