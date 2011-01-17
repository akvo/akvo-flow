package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageDto;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Edits editorial pages and their corresponding EditorialPageContent items.
 * 
 * @author Christopher Fagiani
 * 
 */
public class EditorialPageEditWidget extends Composite implements ContextAware {
	private static final String TEXT_AREA_WIDTH = "600px";
	private static final String TEXT_AREA_HEIGHT = "400px";
	private static final String TOP_ALIGN_LABEL_STYLE = "input-label-topalign";
	
	private Map<String, Object> bundle;
	private EditorialPageDto currentPage;
	private TextBox targetFileNameBox;
	private TextArea template;
	private VerticalPanel contentPanel;
	private VerticalPanel itemPanel;

	public EditorialPageEditWidget() {
		contentPanel = new VerticalPanel();
		itemPanel = new VerticalPanel();
		itemPanel.setVisible(false);
		targetFileNameBox = new TextBox();
		Grid dataGrid = new Grid(2,2);
		
		template = new TextArea();		
		template.setWidth(TEXT_AREA_WIDTH);
		template.setHeight(TEXT_AREA_HEIGHT);
		ViewUtil.installGridRow("File Name", targetFileNameBox,dataGrid,0,0,TOP_ALIGN_LABEL_STYLE);
		ViewUtil.installGridRow("Template Text", template,dataGrid,1,0,TOP_ALIGN_LABEL_STYLE);		
		
		contentPanel.add(dataGrid);
		contentPanel.add(itemPanel);
		initWidget(contentPanel);
	}

	protected void populateFields(EditorialPageDto page) {
		if (page != null) {
			targetFileNameBox.setText(page.getTargetFileName());
			template.setText(page.getTemplate());
		}
	}

	@Override
	public Map<String, Object> getContextBundle() {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		return bundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		currentPage = (EditorialPageDto) bundle
				.get(BundleConstants.EDITORIAL_PAGE);
		populateFields(currentPage);
	}

}
