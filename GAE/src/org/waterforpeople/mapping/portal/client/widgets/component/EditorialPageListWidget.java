package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageDto;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageService;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageServiceAsync;

import com.gallatinsystems.framework.gwt.component.ListBasedWidget;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Lists editorial pages. Clicking the edit button beside an item launches edit
 * mode for that page. Clicking a list item goes to the editorialpageContent
 * editor.
 * 
 * @author Christopher Fagiani
 * 
 */
public class EditorialPageListWidget extends ListBasedWidget implements
		ContextAware {

	private Map<String, Object> bundle;
	private Map<Widget, EditorialPageDto> pageMap;
	private EditorialPageDto editorialPage;
	private EditorialPageServiceAsync editorialPageService;

	public EditorialPageListWidget(PageController controller) {
		super(controller);
		editorialPageService = GWT.create(EditorialPageService.class);
		pageMap = new HashMap<Widget, EditorialPageDto>();
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		loadData();
	}

	public void loadData() {
		toggleLoading(true);
		editorialPageService.listEditorialPage(null,
				new AsyncCallback<List<EditorialPageDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						toggleLoading(false);
					}

					@Override
					public void onSuccess(List<EditorialPageDto> result) {
						if (result != null && result.size() > 0) {
							populateList(result);
						} else {
							toggleLoading(false);
						}
					}
				});

	}

	private void populateList(Collection<EditorialPageDto> pageList) {
		toggleLoading(false);
		Grid dataGrid = new Grid(pageList.size(), 2);
		int i = 0;
		if (pageList != null) {
			for (EditorialPageDto p : pageList) {
				Label l = createListEntry(p.getTargetFileName());
				pageMap.put(l, p);
				Button b = createButton(ClickMode.EDIT, "Edit");
				dataGrid.setWidget(i, 1, b);
				pageMap.put(b, p);
				dataGrid.setWidget(i, 0, l);
				i++;
			}
		}
		addWidget(dataGrid);
	}

	@Override
	protected void handleItemClick(Object source, ClickMode mode) {

		EditorialPageDto page = pageMap.get((Widget) source);
		if (page != null) {
			editorialPage = page;
		}
		if (ClickMode.DELETE != mode) {
			openPage(EditorialPageEditWidget.class, getContextBundle());
		}
	}

	@Override
	public Map<String, Object> getContextBundle() {
		if (editorialPage != null) {
			bundle.put(BundleConstants.EDITORIAL_PAGE, editorialPage);
		}
		return bundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		if (listener != null) {
			listener.operationComplete(true, getContextBundle());
		}
	}

}
