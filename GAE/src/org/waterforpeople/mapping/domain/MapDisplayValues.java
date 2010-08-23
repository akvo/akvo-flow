package org.waterforpeople.mapping.domain;

import com.gallatinsystems.framework.domain.BaseDomain;

public class MapDisplayValues extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 456447006979112919L;
	private MapLevelType mapLevelType = null;
	
	public enum MapLevelType {MAP, FOLDER,PLACEMARK};
	
}
