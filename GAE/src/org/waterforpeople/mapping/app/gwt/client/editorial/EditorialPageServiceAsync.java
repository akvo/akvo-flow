package org.waterforpeople.mapping.app.gwt.client.editorial;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EditorialPageServiceAsync {

	void saveEditorialPage(EditorialPageDto content,
			AsyncCallback<EditorialPageDto> callback);

	void listEditorialPage(String cursor,
			AsyncCallback<List<EditorialPageDto>> callback);

	void listContentByPage(Long pageId,
			AsyncCallback<List<EditorialPageContentDto>> callback);

}
