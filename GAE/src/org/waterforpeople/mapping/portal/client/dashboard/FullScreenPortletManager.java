package org.waterforpeople.mapping.portal.client.dashboard;

import com.google.gwt.core.client.EntryPoint;

public class FullScreenPortletManager extends BaseWFPPortal implements
		EntryPoint {

	private static final int COLUMNS = 1;

	public FullScreenPortletManager() {
		super(COLUMNS);
	}

	@Override
	public Class<?>[] getInvolvedClasses() {
		return new Class[] { this.getClass() };
	}


	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub

	}

}
