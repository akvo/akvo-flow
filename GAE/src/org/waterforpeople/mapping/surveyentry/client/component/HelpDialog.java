package org.waterforpeople.mapping.surveyentry.client.component;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;

public class HelpDialog extends WidgetDialog {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private static final int MAX_IMAGE_HEIGHT = 100;
	private static final int MAX_IMAGE_WIDTH = 100;
	private static final String TITLE = TEXT_CONSTANTS.viewHelp();
	private Grid contentGrid;

	public HelpDialog(List<QuestionHelpDto> helpList, String locale) {
		super(TITLE, null);

		contentGrid = new Grid(helpList.size(), 2);
		int idx = 0;
		for (QuestionHelpDto help : helpList) {
			switch (help.getType()) {
			case PHOTO:
				contentGrid.setWidget(idx, 0,
						ViewUtil.initLabel(help.getLocalizedText(locale)));
				final Image img = new Image(help.getResourceUrl());
				img.addLoadHandler(new LoadHandler() {
					@Override
					public void onLoad(LoadEvent event) {
						Element element = event.getRelativeElement();
						if (element == img.getElement()) {
							int originalHeight = img.getOffsetHeight();
							int originalWidth = img.getOffsetWidth();

							if (originalHeight > MAX_IMAGE_HEIGHT
									|| originalWidth > MAX_IMAGE_WIDTH) {
								img.setPixelSize(MAX_IMAGE_WIDTH,
										originalHeight * MAX_IMAGE_WIDTH
												/ originalWidth);
							}
						}
					}
				});

				contentGrid.setWidget(idx, 1, img);
				break;
			case TEXT:
				contentGrid.setWidget(idx, 0,
						ViewUtil.initLabel(help.getLocalizedText(locale)));
				break;
			default:
				contentGrid.setWidget(idx, 0,
						ViewUtil.initLabel(help.getLocalizedText(locale)));
				contentGrid.setWidget(idx, 1,
						ViewUtil.initLabel(TEXT_CONSTANTS.unsupportedOnWeb()));
			}
			idx++;
		}
		setContentWidget(contentGrid);
	}

}
