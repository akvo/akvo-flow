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
 * Question type that supports taking a picture/video/audio recording with the
 * device's on-board camera.
 * 
 * @author Christopher Fagiani
 * 
 */
public class MediaQuestionView extends QuestionView implements OnClickListener {

	private Button mediaButton;
	private ImageView completeIcon;
	private String mediaType;
	public static final String MEDIA_FILE_KEY = "filename";
	public static final String PHOTO_TYPE = "photo";
	public static final String VIDEO_TYPE = "video";

	public MediaQuestionView(Context context, Question q, String type) {
		super(context, q);
		init(type);
	}

	protected void init(String type) {
		Context context = getContext();
		mediaType = type;
		TableRow tr = new TableRow(context);
		mediaButton = new Button(context);
		if (PHOTO_TYPE.equals(type)) {
			mediaButton.setText(R.string.takephoto);
		} else {
			mediaButton.setText(R.string.takevideo);
		}
		mediaButton.setOnClickListener(this);
		completeIcon = new ImageView(context);
		completeIcon.setImageResource(R.drawable.checkmark);
		completeIcon.setVisibility(View.GONE);
		tr.addView(mediaButton);
		tr.addView(completeIcon);
		addView(tr);
	}

	/**
	 * handle the action button click
	 */
	public void onClick(View v) {
		if (PHOTO_TYPE.equals(mediaType)) {
			notifyQuestionListeners(QuestionInteractionEvent.TAKE_PHOTO_EVENT);
		} else {
			notifyQuestionListeners(QuestionInteractionEvent.TAKE_VIDEO_EVENT);
		}

	}

	@Override
	public void questionComplete(Bundle mediaData) {
		if (mediaData != null) {
			completeIcon.setVisibility(View.VISIBLE);
			setResponse(new QuestionResponse(mediaData
					.getString(MEDIA_FILE_KEY),
					PHOTO_TYPE.equals(mediaType) ? QuestionResponse.IMAGE_TYPE
							: QuestionResponse.VIDEO_TYPE, getQuestion()
							.getId()));
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
