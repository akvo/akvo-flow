package org.waterforpeople.mapping.app.gwt.server.displaytemplate;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.displaytemplate.DisplayTemplateManagerService;
import org.waterforpeople.mapping.app.gwt.client.displaytemplate.MapBalloonDefinitionDto;
import org.waterforpeople.mapping.helper.SpreadsheetMappingAttributeHelper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DisplayTemplateMappingServiceImpl extends RemoteServiceServlet
		implements DisplayTemplateManagerService {

	private static final long serialVersionUID = -4261694426321368183L;

	@Override
	public ArrayList<String> getLabels() {
		ArrayList<String> labels = new ArrayList<String>();
		labels.add("Order");
		labels.add("Description");
		labels.add("Attribute");
		return null;
	}

	@Override
	public void delete(Long keyId) {
		// TODO: delete
	}

	@Override
	public ArrayList<MapBalloonDefinitionDto> getRows() {
		return null;
	}

	@Override
	public ArrayList<String> listObjectAttributes(String objectNames) {
		return SpreadsheetMappingAttributeHelper.listObjectAttributes();
	}

	@Override
	public MapBalloonDefinitionDto save(MapBalloonDefinitionDto item) {
		return null;
	}

}
