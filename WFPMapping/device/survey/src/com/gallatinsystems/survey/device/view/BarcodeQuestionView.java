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
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Question to handle scanning of a barcode. This question relies on the zxing
 * library being installed on the device.
 * 
 * @author Christopher Fagiani
 * 
 */
public class BarcodeQuestionView extends QuestionView implements
		OnClickListener {

	private Button barcodeButton;
	private ImageView completeIcon;

	public BarcodeQuestionView(Context context, Question q) {
		super(context, q);
		init();
	}

	protected void init() {
		Context context = getContext();
		TableRow tr = new TableRow(context);
		barcodeButton = new Button(context);

		barcodeButton.setText(R.string.scanbarcode);

		barcodeButton.setOnClickListener(this);
		completeIcon = new ImageView(context);
		completeIcon.setImageResource(R.drawable.checkmark);
		completeIcon.setVisibility(View.GONE);
		tr.addView(barcodeButton);
		tr.addView(completeIcon);
		addView(tr);
	}

	/**
	 * handle the action button click
	 */
	public void onClick(View v) {
		notifyQuestionListeners(QuestionInteractionEvent.SCAN_BARCODE_EVENT);
	}

	@Override
	public void questionComplete(Bundle barcodeData) {
		if (barcodeData != null) {
			completeIcon.setVisibility(View.VISIBLE);
			setResponse(new QuestionResponse(barcodeData
					.getString(ConstantUtil.BARCODE_CONTENT),
					ConstantUtil.VALUE_RESPONSE_TYPE, getQuestion().getId()));
		}
	}

	/**
	 * restores the data and turns on the complete icon if the
	 * content is non-null
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
