package org.waterforpeople.mapping.surveyentry.client.component;

import java.util.Date;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * simple widget for collecting dates
 * 
 * @author Christopher Fagiani
 * 
 */
public class DateQuestionWidget extends QuestionWidget {
	private static final String TYPE = "DATE";
	private static final DateTimeFormat DATE_FMT = DateTimeFormat
			.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);

	private DateBox dateBox;

	public DateQuestionWidget(QuestionDto q, QuestionAnswerStoreDto a) {
		super(q, a);
	}

	@Override
	protected void constructResponseUi() {
		dateBox = new DateBox();
		dateBox.setFormat(new DateBox.Format() {

			@Override
			public String format(DateBox dateBox, Date date) {
				String result = null;
				if (date != null) {
					try {
						result = DATE_FMT.format(date);
					} catch (Exception e) {
						// no-op
					}
				}
				return result;
			}

			@Override
			public Date parse(DateBox dateBox, String text, boolean reportError) {
				Date result = null;
				try {
					result = DATE_FMT.parse(text);
				} catch (Exception e) {
					// no-op
				}

				return result;
			}

			@Override
			public void reset(DateBox dateBox, boolean abandon) {
				// no-op

			}
		});
		ViewUtil.installFieldRow(getPanel(), "", dateBox, null);

		if (getAnswer().getKeyId() != null) {
			// if we're initializing and key id is not null, pre-populate
			String val = getAnswer().getValue();
			if (val != null && val.trim().length() > 0) {
				try {
					long millis = new Long(val.trim());
					dateBox.setValue(new Date(millis));
				} catch (NumberFormatException e) {
					// no-op
				}
			}

		}
	}

	public void captureAnswer() {
		getAnswer().setType(TYPE);

		if (dateBox.getValue() != null) {
			getAnswer().setValue(dateBox.getValue().getTime() + "");
		} else {
			getAnswer().setValue(null);
		}
	}

	@Override
	protected void resetUi() {
		dateBox.setValue(null);
	}

}
