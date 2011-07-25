package com.gallatinsystems.survey.device.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableRow;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Question for capturing a date (no time component). Once selected, the date
 * will be displayed on the screen using the locale-specific date format
 * (obtained via SimpleDateFormat.getDateInstance()). Though the actual value
 * saved in the response object will be a timestamp (milliseconds since
 * Midnight, Jan 1, 1970 UTC).
 * 
 * @author Christohper Fagiani
 * 
 */
public class DateQuestionView extends QuestionView {

	private EditText dateTextEdit;
	private Button pickButton;
	private int curYear;
	private int curMonth;
	private int curDay;
	private DateFormat dateFormat;
	private Date selectedDate;
	private Calendar calendar;

	public DateQuestionView(Context context, Question q, String defaultLang,
			String[] langCodes, boolean readOnly) {
		super(context, q, defaultLang, langCodes, readOnly);
		calendar = Calendar.getInstance();
		curYear = calendar.get(Calendar.YEAR);
		curMonth = calendar.get(Calendar.MONTH);
		curDay = calendar.get(Calendar.DAY_OF_MONTH);
		dateFormat = SimpleDateFormat.getDateInstance();
		init();
	}

	protected void init() {
		Context context = getContext();
		TableRow tr = new TableRow(context);
		dateTextEdit = new EditText(context);
		dateTextEdit.setWidth(DEFAULT_WIDTH);
		dateTextEdit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

		dateTextEdit.setFocusable(false);

		pickButton = new Button(context);
		pickButton.setText(R.string.pickdate);
		pickButton.setWidth(DEFAULT_WIDTH);
		pickButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedDate != null) {
					Calendar c = new GregorianCalendar();
					c.setTime(selectedDate);
					curDay = c.get(Calendar.DAY_OF_MONTH);
					curYear = c.get(Calendar.YEAR);
					curMonth = c.get(Calendar.MONTH);
				}
				DatePickerDialog dia = new DatePickerDialog(getContext(),
						new OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								selectedDate = new GregorianCalendar(year,
										monthOfYear, dayOfMonth).getTime();
								dateTextEdit.setText(dateFormat
										.format(selectedDate));
								captureResponse();
							}
						}, curYear, curMonth, curDay);
				dia.show();
			}
		});

		dateTextEdit.setWidth(screenWidth - 50);

		tr.addView(dateTextEdit);
		addView(tr);
		tr = new TableRow(context);
		tr.addView(pickButton);
		addView(tr);
	}

	/**
	 * pulls the data out of the fields and saves it as a response object
	 */
	@Override
	public void captureResponse() {
		captureResponse(false);
	}

	@Override
	public void setResponse(QuestionResponse resp) {
		if (resp != null && dateTextEdit != null) {
			if (resp.getValue() != null && resp.getValue().trim().length() > 0) {
				selectedDate = new Date(Long.parseLong(resp.getValue()));

				dateTextEdit.setText(dateFormat.format(selectedDate));
			} else {
				selectedDate = null;
				dateTextEdit.setText("");
			}

		}
		super.setResponse(resp);
	}

	/**
	 * pulls the data out of the fields and saves it as a response object,
	 * possibly suppressing listeners
	 */
	@Override
	public void captureResponse(boolean suppressListeners) {
		setResponse(
				new QuestionResponse(selectedDate != null ? selectedDate
						.getTime() + "" : "", ConstantUtil.DATE_RESPONSE_TYPE,
						getQuestion().getId()),
				suppressListeners);
	}

	@Override
	public void rehydrate(QuestionResponse resp) {
		super.rehydrate(resp);
		if (resp != null && dateTextEdit != null) {
			if (resp.getValue() != null && resp.getValue().trim().length() > 0) {
				selectedDate = new Date(Long.parseLong(resp.getValue()));
				dateTextEdit.setText(dateFormat.format(selectedDate));
			} else {
				selectedDate = null;
				dateTextEdit.setText("");
			}
		}
	}

	@Override
	public void resetQuestion(boolean fireEvent) {
		super.resetQuestion(fireEvent);
		dateTextEdit.setText("");
		selectedDate = null;
	}
}
