package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.portal.client.widgets.component.AdminHomeWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.PublicationWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.QuestionEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.QuestionGroupEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.QuestionGroupListWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.QuestionListWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyGroupEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyGroupListWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyListWidget;

import com.gallatinsystems.framework.gwt.wizard.client.AbstractWizardPortlet;
import com.google.gwt.user.client.ui.Widget;

public class AdminWizardPortlet extends AbstractWizardPortlet {
	public static final String DESCRIPTION = "Provides system administration ui";
	public static final String NAME = "Admin Wizard";
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;

	public AdminWizardPortlet() {
		super(NAME, WIDTH, HEIGHT);
	}

	protected WizardWorkflow getWizardWorkflow() {
		WizardWorkflow wf = new WizardWorkflow();
		wf.setStartNode(new WizardNode("Administration", "Administration Home",
				AdminHomeWidget.class, (String)null, (String)null));
		wf.addInternalNode(new WizardNode("SurveyGroupList", "Survey Groups",
				SurveyGroupListWidget.class, "SurveyGroupCreate",
				"Administration"));
		wf.addInternalNode(new WizardNode("SurveyGroupCreate", null,
				SurveyGroupEditWidget.class, "SurveyList", "SurveyGroupList"));
		wf.addInternalNode(new WizardNode("SurveyList", "Surveys",
				SurveyListWidget.class, "SurveyCreate", "SurveyGroupList"));
		wf.addInternalNode(new WizardNode("SurveyCreate", null,
				SurveyEditWidget.class, "QuestionGroupList", "SurveyList"));
		wf.addInternalNode(new WizardNode("QuestionGroupList",
				"Question Groups", QuestionGroupListWidget.class,
				"QuestionGroupCreate", "SurveyList"));
		wf.addInternalNode(new WizardNode("QuestionGroupCreate", null,
				QuestionGroupEditWidget.class, "QuestionList",
				"QuestionGroupList"));
		wf.addInternalNode(new WizardNode("QuestionList", "Questions",
				QuestionListWidget.class, "QuestionCreate", "QuestionGroupList"));
		wf.addInternalNode(new WizardNode("QuestionCreate", null,
				QuestionEditWidget.class, new String[] { "Question List" },
				new String[] { "QuestionList", "Publish" }));
		wf.addInternalNode(new WizardNode("Publish", null,
				PublicationWidget.class, new String[] { "Attribute Assignment",
						"Device Assignment" }, new String[] { "QuestionList" }));
		return wf;

	}

	public String getName() {
		return NAME;
	}

	@Override
	protected Widget initializeNode(WizardNode node) {
		if(node.getWidgetClass().equals(AdminHomeWidget.class)){
			return new AdminHomeWidget(this);
		}else if (node.getWidgetClass().equals(PublicationWidget.class)){
			return new PublicationWidget();
	}else if (node.getWidgetClass().equals(QuestionEditWidget.class)){
			return new QuestionEditWidget();
	}else if (node.getWidgetClass().equals(QuestionGroupEditWidget.class)){
			return new QuestionGroupEditWidget();
	}else if (node.getWidgetClass().equals(QuestionGroupListWidget.class)){
			return new QuestionGroupListWidget();
	}else if (node.getWidgetClass().equals(SurveyEditWidget.class)){
			return new SurveyEditWidget();
	}else if (node.getWidgetClass().equals(SurveyGroupEditWidget.class)){
			return new SurveyGroupEditWidget();
	}else if (node.getWidgetClass().equals(SurveyGroupListWidget.class)){
			return new SurveyGroupListWidget();
	}else if (node.getWidgetClass().equals(SurveyListWidget.class)){
			return new SurveyListWidget();
	}
		return null;

	}

	@Override
	protected void onLoadComplete(WizardNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void prePageUnload(WizardNode nextNode) {
		// TODO Auto-generated method stub

	}

}
