package com.gallatinsystems.standards.domain;

import java.util.Date;

import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.standards.domain.Standard.StandardScope;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.google.appengine.api.datastore.Key;

public interface StandardDef {
	public void setKey(Key key);
	public Date getEffectiveStartDate();

	public void setEffectiveStartDate(Date effectiveStartDate) ;

	public Date getEffectiveEndDate();

	public void setEffectiveEndDate(Date effectiveEndDate);

	public String getStandardDescription();

	public void setStandardDescription(String standardDescription);

	public AccessPointType getAccessPointType() ;

	public void setAccessPointType(AccessPointType accessPointType) ;

	public StandardType getStandardType() ;

	public void setStandardType(StandardType standardType) ;

	public String getCountryCode();

	public void setCountryCode(String countryCode);

	public StandardScope getStandardScope() ;

	public void setStandardScope(StandardScope standardScope);

}
