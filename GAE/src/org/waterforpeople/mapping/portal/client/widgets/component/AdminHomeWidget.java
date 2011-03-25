package org.waterforpeople.mapping.portal.client.widgets.component;

import org.waterforpeople.mapping.app.gwt.client.util.PermissionConstants;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.MenuBasedWidget;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;

/**
 * Widget meant to be used as the "Home" screen in a wizard. It presents a
 * number of buttons to launch semi-linear workflows for common administrative
 * tasks (user management, survey creation, etc).
 * 
 * @author Christopher Fagiani
 * 
 */
public class AdminHomeWidget extends MenuBasedWidget {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private Button userMgmtButton;
	private Button surveyMgmtButton;
	private Button assignmentButton;
	private Button mappingButton;
	private Button editorialButton;
	private Button generateBootstrapButton;
	private Button editWebAuthButton;
	private Button reportButton;
	private Button importButton;
	private PageController controller;

	public AdminHomeWidget(PageController controller, UserDto user) {
		Grid widget = new Grid(9, 2);
		this.controller = controller;
		userMgmtButton = initButton(TEXT_CONSTANTS.manageUsers());

		widget.setWidget(0, 0, userMgmtButton);
		widget.setWidget(0, 1, createDescription(TEXT_CONSTANTS
				.manageUsersDescription()));
		if (!user.hasPermission(PermissionConstants.EDIT_USER)) {
			userMgmtButton.setEnabled(false);
		}

		surveyMgmtButton = initButton(TEXT_CONSTANTS.manageSurveys());
		if (!user.hasPermission(PermissionConstants.EDIT_SURVEY)) {
			surveyMgmtButton.setEnabled(false);
		}
		widget.setWidget(1, 0, surveyMgmtButton);
		widget
				.setWidget(1, 1,
						createDescription(TEXT_CONSTANTS.manageSurveysDescription()));
		assignmentButton = initButton(TEXT_CONSTANTS.assignSurveys());
		if (!user.hasPermission(PermissionConstants.EDIT_SURVEY)) {
			assignmentButton.setEnabled(false);
		}

		widget.setWidget(2, 0, assignmentButton);
		widget
				.setWidget(
						2,
						1,
						createDescription(TEXT_CONSTANTS.assignSurveysDescription()));
		mappingButton = initButton(TEXT_CONSTANTS.mapAPAttributes());
		if (!user.hasPermission(PermissionConstants.EDIT_SURVEY)) {
			mappingButton.setEnabled(false);
		}
		widget.setWidget(3, 0, mappingButton);
		widget
				.setWidget(
						3,
						1,
						createDescription(TEXT_CONSTANTS.mapAPAttributesDescription()));

		editorialButton = initButton(TEXT_CONSTANTS.editEditorial());
		if (!user.hasPermission(PermissionConstants.EDIT_EDITORIAL)) {
			editorialButton.setEnabled(false);
		}
		widget.setWidget(4, 0, editorialButton);
		widget
				.setWidget(
						4,
						1,
						createDescription(TEXT_CONSTANTS.editEditorialDescription()));

		generateBootstrapButton = initButton(TEXT_CONSTANTS.generateBootstrap());
		if (!user.hasPermission(PermissionConstants.EDIT_SURVEY)) {
			generateBootstrapButton.setEnabled(false);
		}
		widget.setWidget(5, 0, generateBootstrapButton);
		widget
				.setWidget(
						5,
						1,
						createDescription(TEXT_CONSTANTS.generateBootstrapDescription()));

		editWebAuthButton = initButton(TEXT_CONSTANTS.editWebAuth());
		if (!user.hasPermission(PermissionConstants.EDIT_TOKENS)) {
			editWebAuthButton.setEnabled(false);
		}
		widget.setWidget(6, 0, editWebAuthButton);
		widget
				.setWidget(
						6,
						1,
						createDescription(TEXT_CONSTANTS.editWebAuthDescription()));

		reportButton = initButton(TEXT_CONSTANTS.runReports());
		if (!user.hasPermission(PermissionConstants.RUN_REPORTS)) {
			reportButton.setEnabled(false);
		}
		widget.setWidget(7, 0, reportButton);
		widget
				.setWidget(
						7,
						1,
						createDescription(TEXT_CONSTANTS.runReportsDescription()));

		importButton = initButton(TEXT_CONSTANTS.importData());
		if (!user.hasPermission(PermissionConstants.IMPORT_DATA)) {
			importButton.setEnabled(false);
		}
		widget.setWidget(8, 0, importButton);
		widget.setWidget(8, 1,
				createDescription(TEXT_CONSTANTS.importDataDescription()));

		initWidget(widget);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == surveyMgmtButton) {
			controller.openPage(SurveyGroupListWidget.class, null);
		} else if (event.getSource() == userMgmtButton) {
			controller.openPage(UserManagerWidget.class, null);
		} else if (event.getSource() == mappingButton) {
			controller.openPage(AttributeAssignmentWidget.class, null);
		} else if (event.getSource() == editorialButton) {
			controller.openPage(EditorialPageListWidget.class, null);
		} else if (event.getSource() == generateBootstrapButton) {
			controller.openPage(BootstrapGeneratorWidget.class, null);
		} else if (event.getSource() == assignmentButton) {
			controller.openPage(SurveyAssignmentListWidget.class, null);
		} else if (event.getSource() == editWebAuthButton) {
			controller.openPage(WebActivityAuthorizationListWidget.class, null);
		} else if (event.getSource() == reportButton) {
			controller.openPage(RunReportWidget.class, null);
		} else if (event.getSource() == importButton) {
			controller.openPage(DataImportWidget.class, null);
		}
	}

}
