package org.waterforpeople.mapping.app.gwt.server.displaytemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.displaytemplate.DisplayTemplateManagerService;
import org.waterforpeople.mapping.app.gwt.client.displaytemplate.DisplayTemplateMappingDto;
import org.waterforpeople.mapping.app.util.DisplayTemplateMappingUtil;
import org.waterforpeople.mapping.dao.DisplayTemplateMappingDAO;
import org.waterforpeople.mapping.domain.DisplayTemplateMapping;
import org.waterforpeople.mapping.helper.SpreadsheetMappingAttributeHelper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DisplayTemplateMappingServiceImpl extends RemoteServiceServlet
		implements DisplayTemplateManagerService {

	DisplayTemplateMappingDAO dao = new DisplayTemplateMappingDAO();

	/**
	 * 
	 */
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
		//TODO: delete
	}

	@Override
	public ArrayList<DisplayTemplateMappingDto> getRows() {
		ArrayList<DisplayTemplateMappingDto> list = new ArrayList<DisplayTemplateMappingDto>();
		for (DisplayTemplateMapping itemFrom : dao.list("all")) {
			DisplayTemplateMappingDto itemTo = new DisplayTemplateMappingDto();
			try {
				DisplayTemplateMappingUtil.copyCanonicalToDto(itemFrom, itemTo);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(itemTo);
		}
		return list;
	}

	@Override
	public ArrayList<String> listObjectAttributes(String objectNames) {
		return SpreadsheetMappingAttributeHelper.listObjectAttributes();
	}

	@Override
	public DisplayTemplateMappingDto save(DisplayTemplateMappingDto item) {
		DisplayTemplateMapping itemTo = new DisplayTemplateMapping();
		try {
			DisplayTemplateMappingUtil.copyDtoToCanonical(item, itemTo);
			DisplayTemplateMappingUtil.copyCanonicalToDto(dao.save(itemTo),
					item);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return item;
	}

}
