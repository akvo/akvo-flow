package org.waterforpeople.mapping.app.gwt.client.editorial;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for persisting/finding editorial content. this can be used with
 * velocity templates to generate static html pages using data in the system.
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("editorialcontentrpcservice")
public interface EditorialPageService extends RemoteService {

	/**
	 * persists a page and any PageCotnentDto's contained therein
	 * 
	 * @param content
	 * @return
	 */
	public EditorialPageDto saveEditorialPage(EditorialPageDto content);

	/**
	 * finds all editorial pages
	 * 
	 * @param cursor
	 * @return
	 */
	public List<EditorialPageDto> listEditorialPage(String cursor);

	/**
	 * finds all EditorialPageContentDto items on a given page
	 * 
	 * @param pageId
	 * @return
	 */
	public List<EditorialPageContentDto> listContentByPage(Long pageId);
}
