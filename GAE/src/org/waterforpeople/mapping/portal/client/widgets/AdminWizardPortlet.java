package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.AdminHomeWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.AttributeAssignmentWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.BootstrapGeneratorWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.ChangeCompleteWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.DataImportWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.EditorialPageEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.EditorialPageListWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.MetricMappingWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.PublicationWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.QuestionEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.QuestionGroupEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.QuestionGroupListWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.QuestionListWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.RerunMappingWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.RunReportWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyAssignmentEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyAssignmentListWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyGroupEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyGroupListWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyListWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyWebActivityAuthorizationEditWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.UserManagerWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.WebActivityAuthorizationListWidget;

import com.gallatinsystems.framework.gwt.wizard.client.AbstractWizardPortlet;
import com.gallatinsystems.user.app.gwt.client.PermissionConstants;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

/**
 * Portlet to integrate the admin wizard with the portal framework
 * 
 * @author Christopher Fagiani
 * 
 */
public class AdminWizardPortlet extends AbstractWizardPortlet {
	private static final String LOCALE_DOMAIN = "locale";
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String DESCRIPTION = TEXT_CONSTANTS
			.adminWizardPortletDescription();
	public static final String NAME = TEXT_CONSTANTS.adminWizardPortletTitle();;
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	private UserDto user;
	private String domainType;

	public AdminWizardPortlet(UserDto user, String domainType) {
		super(NAME, WIDTH, HEIGHT, user);
		this.user = user;
		this.domainType = domainType;
		init();
	}

	protected WizardWorkflow getWizardWorkflow() {
		WizardWorkflow wf = new WizardWorkflow();
		wf.setStartNode(new WizardNode("Administration", TEXT_CONSTANTS
				.adminHome(), AdminHomeWidget.class, (WizardButton) null,
				(WizardButton) null));
		wf.addInternalNode(new WizardNode("SurveyGroupList", TEXT_CONSTANTS
				.surveyGroups(), SurveyGroupListWidget.class, new WizardButton(
				"SurveyGroupCreate", TEXT_CONSTANTS.createSurveyGroup()),
				new WizardButton("Administration", TEXT_CONSTANTS
						.backToAdminHome())));
		wf.addInternalNode(new WizardNode("SurveyGroupCreate", null,
				SurveyGroupEditWidget.class, new WizardButton("SurveyList",
						TEXT_CONSTANTS.saveAndContinue()), new WizardButton(
						"SurveyGroupList", TEXT_CONSTANTS
								.backToSurveyGroupList())));
		wf.addInternalNode(new WizardNode("SurveyList", TEXT_CONSTANTS
				.surveys(), SurveyListWidget.class, new WizardButton(
				"SurveyCreate", TEXT_CONSTANTS.createSurvey()),
				new WizardButton("SurveyGroupList", TEXT_CONSTANTS
						.backToSurveyGroupList())));
		wf.addInternalNode(new WizardNode("SurveyCreate", null,
				SurveyEditWidget.class, new WizardButton("QuestionGroupList",
						TEXT_CONSTANTS.saveAndContinue()), new WizardButton(
						"SurveyList", TEXT_CONSTANTS.backToSurveyList())));
		wf.addInternalNode(new WizardNode("QuestionGroupList", TEXT_CONSTANTS
				.questionGroups(), QuestionGroupListWidget.class,
				new WizardButton[] {
						new WizardButton("QuestionGroupCreate", TEXT_CONSTANTS
								.createQuestionGroup()),
						new WizardButton("Changes Complete", TEXT_CONSTANTS
								.markChangesComplete()),
						new WizardButton("Publish", TEXT_CONSTANTS
								.publishSurvey(),
								PermissionConstants.PUBLISH_SURVEY) },
				new WizardButton[] { new WizardButton("SurveyList",
						TEXT_CONSTANTS.backToSurveyList()) }));
		wf.addInternalNode(new WizardNode("QuestionGroupCreate", null,
				QuestionGroupEditWidget.class, new WizardButton("QuestionList",
						TEXT_CONSTANTS.saveAndContinue()), new WizardButton(
						"QuestionGroupList", TEXT_CONSTANTS
								.backToQuestionGroupList())));
		wf.addInternalNode(new WizardNode("QuestionList", TEXT_CONSTANTS
				.questions(), QuestionListWidget.class, new WizardButton[] {
				new WizardButton("QuestionCreate", TEXT_CONSTANTS
						.createQuestion()),
				new WizardButton("Changes Complete", TEXT_CONSTANTS
						.markChangesComplete()),
				new WizardButton("Publish", TEXT_CONSTANTS.publishSurvey(),
						PermissionConstants.PUBLISH_SURVEY) },
				new WizardButton[] { new WizardButton("QuestionGroupList",
						TEXT_CONSTANTS.backToQuestionGroupList()) }));
		wf.addInternalNode(new WizardNode("QuestionCreate", null,
				QuestionEditWidget.class, new WizardButton("QuestionCreate",
						TEXT_CONSTANTS.saveAndContinue()), new WizardButton(
						"QuestionList", TEXT_CONSTANTS.backToQuestionList())));
		wf.addInternalNode(new WizardNode("Publish", null,
				PublicationWidget.class, new WizardButton[] { new WizardButton(
						"Attribute Assignment", TEXT_CONSTANTS
								.assignQuestionsToAttributes()) },
				new WizardButton[] { new WizardButton("QuestionList",
						TEXT_CONSTANTS.backToQuestionList()) }));
		wf.addInternalNode(new WizardNode("Changes Complete", null,
				ChangeCompleteWidget.class,
				new WizardButton[] { new WizardButton("Administration",
						TEXT_CONSTANTS.adminHome()) },
				new WizardButton[] { new WizardButton("QuestionList",
						TEXT_CONSTANTS.backToQuestionList()) }));
		wf.addInternalNode(new WizardNode("Attribute Assignment",
				TEXT_CONSTANTS.attributeAssignment(),
				AttributeAssignmentWidget.class, new WizardButton(
						"Assignment List", TEXT_CONSTANTS
								.assignSurveysToDevices()), new WizardButton(
						"Administration", TEXT_CONSTANTS.backToAdminHome())));
		wf.addInternalNode(new WizardNode("User Management", null,
				UserManagerWidget.class, (WizardButton) null,
				(WizardButton) null));
		wf.addInternalNode(new WizardNode("Editorial Page List", null,
				EditorialPageListWidget.class, new WizardButton("Create Page",
						TEXT_CONSTANTS.createEditorialPage()),
				new WizardButton("Administration", TEXT_CONSTANTS
						.backToAdminHome())));
		wf.addInternalNode(new WizardNode("Create Page", null,
				EditorialPageEditWidget.class,
				new WizardButton("Editorial Page List", TEXT_CONSTANTS
						.saveAndContinue()), new WizardButton(
						"Editorial Page List", TEXT_CONSTANTS
								.backToEditorialPageList())));
		wf.addInternalNode(new WizardNode("Generate Bootstrap File", null,
				BootstrapGeneratorWidget.class, (WizardButton) null,
				(WizardButton) null));
		wf.addInternalNode(new WizardNode("Assignment List", TEXT_CONSTANTS
				.assignmentList(), SurveyAssignmentListWidget.class,
				new WizardButton("Edit Assignment", TEXT_CONSTANTS
						.createAssignment()), new WizardButton(
						"Administration", TEXT_CONSTANTS.backToAdminHome())));
		wf.addInternalNode(new WizardNode("Edit Assignment", TEXT_CONSTANTS
				.editAssignment(), SurveyAssignmentEditWidget.class,
				new WizardButton("Administration", TEXT_CONSTANTS
						.saveAndContinue()), new WizardButton(
						"Assignment List", TEXT_CONSTANTS
								.backToAssignmentList())));
		wf.addInternalNode(new WizardNode("List Web Authorizations",
				TEXT_CONSTANTS.webAuthList(),
				WebActivityAuthorizationListWidget.class, new WizardButton(
						"Edit Web Authorization", TEXT_CONSTANTS
								.createNewAuth()), new WizardButton(
						"Administration", TEXT_CONSTANTS.backToAdminHome())));
		wf.addInternalNode(new WizardNode("Edit Web Authorization",
				TEXT_CONSTANTS.editWebAuth(),
				SurveyWebActivityAuthorizationEditWidget.class,
				new WizardButton("List Web Authorizations", TEXT_CONSTANTS
						.saveAndContinue()), new WizardButton(
						"List Web Authorizations", TEXT_CONSTANTS
								.backToAuthorizationList())));
		wf.addInternalNode(new WizardNode("Run Reports", TEXT_CONSTANTS
				.runReports(), RunReportWidget.class, null, new WizardButton(
				"Administration", TEXT_CONSTANTS.backToAdminHome())));
		wf.addInternalNode(new WizardNode("Import Data", TEXT_CONSTANTS
				.importData(), DataImportWidget.class, null, new WizardButton(
				"Administration", TEXT_CONSTANTS.backToAdminHome())));
		wf.addInternalNode(new WizardNode("Rerun AP Mapping", null,
				RerunMappingWidget.class, null, new WizardButton(
						"Administration", TEXT_CONSTANTS.backToAdminHome())));
		wf.addInternalNode(new WizardNode("Map Metrics", null,
				MetricMappingWidget.class, new WizardButton("Assignment List",
						TEXT_CONSTANTS.assignSurveysToDevices()),
				new WizardButton("Administration", TEXT_CONSTANTS
						.backToAdminHome())));
		return wf;
	}

	public String getName() {
		return NAME;
	}

	@Override
	protected Widget initializeNode(WizardNode node) {
		if (node.getWidgetClass().equals(AdminHomeWidget.class)) {
			return new AdminHomeWidget(this, user);
		} else if (node.getWidgetClass().equals(PublicationWidget.class)) {
			return new PublicationWidget();
		} else if (node.getWidgetClass().equals(QuestionEditWidget.class)) {
			return new QuestionEditWidget(user,this);
		} else if (node.getWidgetClass().equals(QuestionGroupEditWidget.class)) {
			return new QuestionGroupEditWidget(this);
		} else if (node.getWidgetClass().equals(QuestionGroupListWidget.class)) {
			return new QuestionGroupListWidget(this);
		} else if (node.getWidgetClass().equals(SurveyEditWidget.class)) {
			return new SurveyEditWidget();
		} else if (node.getWidgetClass().equals(SurveyGroupEditWidget.class)) {
			return new SurveyGroupEditWidget();
		} else if (node.getWidgetClass().equals(SurveyGroupListWidget.class)) {
			return new SurveyGroupListWidget(this);
		} else if (node.getWidgetClass().equals(SurveyListWidget.class)) {
			return new SurveyListWidget(this);
		} else if (node.getWidgetClass().equals(QuestionListWidget.class)) {
			return new QuestionListWidget(this);
		} else if (node.getWidgetClass().equals(UserManagerWidget.class)) {
			return new UserManagerWidget();
		} else if (node.getWidgetClass()
				.equals(AttributeAssignmentWidget.class)) {
			if(LOCALE_DOMAIN.equalsIgnoreCase(domainType)){
				return new MetricMappingWidget();
			}else{
				return new AttributeAssignmentWidget();
			}
		} else if (node.getWidgetClass().equals(EditorialPageEditWidget.class)) {
			return new EditorialPageEditWidget();
		} else if (node.getWidgetClass().equals(EditorialPageListWidget.class)) {
			return new EditorialPageListWidget(this);
		} else if (node.getWidgetClass().equals(BootstrapGeneratorWidget.class)) {
			return new BootstrapGeneratorWidget();
		} else if (node.getWidgetClass().equals(
				SurveyAssignmentEditWidget.class)) {
			return new SurveyAssignmentEditWidget();
		} else if (node.getWidgetClass().equals(
				SurveyAssignmentListWidget.class)) {
			return new SurveyAssignmentListWidget(this);
		} else if (node.getWidgetClass().equals(
				SurveyWebActivityAuthorizationEditWidget.class)) {
			return new SurveyWebActivityAuthorizationEditWidget();
		} else if (node.getWidgetClass().equals(
				WebActivityAuthorizationListWidget.class)) {
			return new WebActivityAuthorizationListWidget(this, user);
		} else if (node.getWidgetClass().equals(RunReportWidget.class)) {
			return new RunReportWidget();
		} else if (node.getWidgetClass().equals(DataImportWidget.class)) {
			return new DataImportWidget();
		} else if (node.getWidgetClass().equals(RerunMappingWidget.class)) {
			return new RerunMappingWidget();
		} else if (node.getWidgetClass().equals(ChangeCompleteWidget.class)) {
			return new ChangeCompleteWidget();
		} else if (node.getWidgetClass().equals(MetricMappingWidget.class)) {
			return new MetricMappingWidget();
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
