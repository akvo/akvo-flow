package org.waterforpeople.mapping.app.gwt.server.editorial;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageContentDto;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageDto;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.editorial.dao.EditorialPageDao;
import com.gallatinsystems.editorial.domain.EditorialPage;
import com.gallatinsystems.editorial.domain.EditorialPageContent;
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
	private EditorialPageDao editorialDao;

	public EditorialContentServiceImpl() {
		super();
		editorialDao = new EditorialPageDao();
	}

	@Override
	public List<EditorialPageContentDto> listContentByPage(Long pageId) {
		List<EditorialPageContent> content = editorialDao
				.listContentByPage(pageId);
		List<EditorialPageContentDto> dtoList = null;
		if (content != null) {
			dtoList = new ArrayList<EditorialPageContentDto>();
			for (EditorialPageContent contentItem : content) {
				EditorialPageContentDto dto = new EditorialPageContentDto();
				DtoMarshaller.copyToDto(contentItem, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	@Override
	public List<EditorialPageDto> listEditorialPage(String cursor) {
		List<EditorialPage> pages = editorialDao.list(cursor);
		List<EditorialPageDto> dtoList = null;
		if (pages != null) {
			dtoList = new ArrayList<EditorialPageDto>();
			for (EditorialPage page : pages) {
				EditorialPageDto dto = new EditorialPageDto();
				DtoMarshaller.copyToDto(page, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	@Override
	public EditorialPageDto saveEditorialPage(EditorialPageDto content) {
		// TODO Auto-generated method stub
		return null;
	}

}
