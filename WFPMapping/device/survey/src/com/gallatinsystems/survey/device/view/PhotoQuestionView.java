package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.event.QuestionInteractionEvent;

/**
 * Question type that supports taking a picture with the device's on-board
 * camera.
 * 
 * @author Christopher Fagiani
 * 
 */
public class PhotoQuestionView extends QuestionView implements OnClickListener {

	private Button photoButton;
	private ImageView completeIcon;
	public static final String PHOTO_FILE_KEY = "filename";

	public PhotoQuestionView(Context context, Question q) {
		super(context,q);
		init();
	}

	protected void init() {
		Context context = getContext();
		TableRow tr = new TableRow(context);
		photoButton = new Button(context);
		photoButton.setText(R.string.takephoto);
		photoButton.setOnClickListener(this);
		completeIcon = new ImageView(context);
		completeIcon.setImageResource(R.drawable.checkmark);
		completeIcon.setVisibility(View.GONE);
		tr.addView(photoButton);
		tr.addView(completeIcon);
		addView(tr);
	}

	public void onClick(View v) {
		notifyQuestionListeners(QuestionInteractionEvent.TAKE_PHOTO_EVENT);
	}

	@Override
	public void questionComplete(Bundle photoData) {
		if (photoData != null) {
			completeIcon.setVisibility(View.VISIBLE);
			setResponse(new QuestionResponse(photoData
					.getString(PHOTO_FILE_KEY), QuestionResponse.IMAGE_TYPE,
					getQuestion().getId()));
		}
	}

	/**
	 * restores the file path for the file and turns on the complete icon if the
	 * file exists
	 */
	public void rehydrate(QuestionResponse resp) {
		super.rehydrate(resp);
		if (resp != null) {
			if (resp.getValue() != null) {
				completeIcon.setVisibility(View.VISIBLE);
			}

		}
	}

	/**
	 * clears the file path and the complete icon
	 */
	public void resetQuestion() {
		super.resetQuestion();
		completeIcon.setVisibility(View.GONE);
	}
}
