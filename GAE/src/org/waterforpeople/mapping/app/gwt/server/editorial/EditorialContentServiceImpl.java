package org.waterforpeople.mapping.app.gwt.server.editorial;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageContentDto;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageDto;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service for saving/finding editorial page objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class EditorialContentServiceImpl extends RemoteServiceServlet implements
		EditorialPageService {

	private static final long serialVersionUID = 1631722278637197282L;

	@Override
	public List<EditorialPageContentDto> listContentByPage(Long pageId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditorialPageDto> listEditorialPage(String cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorialPageDto saveEditorialPage(EditorialPageDto content) {
		// TODO Auto-generated method stub
		return null;
	}

}
