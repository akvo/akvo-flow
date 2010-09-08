package com.gallatinsystems.framework.gwt.util.client;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class DetailPanelUtil<T extends BaseDto> {
	HorizontalPanel panel = null;
	public HorizontalPanel createAndBindObject(Class<T> sourceObject){
		return panel;
	}
}
