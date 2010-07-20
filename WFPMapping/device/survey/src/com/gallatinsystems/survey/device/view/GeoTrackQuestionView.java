package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.event.QuestionInteractionEvent;

/**
 *This question view handles geographic tracking operations (a stop/start
 * button that toggles the recording of way points).
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoTrackQuestionView extends QuestionView implements
		OnClickListener {

	private static final int BUTTON_WIDTH = 75;
	private Button trackButton;
	private boolean isRunning;

	public GeoTrackQuestionView(Context context, Question q, String[] langs,
			boolean readOnly) {
		super(context, q, langs, readOnly);
		trackButton = new Button(context);
		trackButton.setText(R.string.starttrack);
		trackButton.setOnClickListener(this);
		trackButton.setWidth(BUTTON_WIDTH);
		TableRow tr = new TableRow(context);
		tr.addView(trackButton);
		addView(tr);
		isRunning = false;
	}

	/**
	 * starts or stops the background service to record track information and
	 * fires an event to tell the survey view whether or not to allow survey
	 * submission/save/clear
	 * 
	 */
	@Override
	public void onClick(View v) {
		if (!isRunning) {
			notifyQuestionListeners(QuestionInteractionEvent.START_TRACK);
			trackButton.setText(R.string.endtrack);
			isRunning = true;
		} else {
			notifyQuestionListeners(QuestionInteractionEvent.END_TRACK);
			trackButton.setText(R.string.starttrack);
			isRunning = false;
		}
	}

}
