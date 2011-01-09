package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.Map;

import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AttributeAssignmentWidget extends Composite implements ContextAware{

	private VerticalPanel contentPanel;
	private ListBox questionGroupSelector; 
	
	public AttributeAssignmentWidget(){
		contentPanel = new VerticalPanel();
		questionGroupSelector = new ListBox();
		initWidget(contentPanel);
	}
	
	@Override
	public Map<String, Object> getContextBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		// TODO Auto-generated method stub
		
	}

}
