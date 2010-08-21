package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.event.QuestionInteractionEvent;
import com.gallatinsystems.survey.device.service.GeoTrackService;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;

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
	private GeoTrackService geoTrackService;
	private ServiceConnection connection;

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
		configureConnection();
	}

	/**
	 * sets up the service connection callbacks so we can bind to the instance
	 * of the geo track service that we'll start later
	 */
	private void configureConnection() {
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				geoTrackService = ((GeoTrackService.GeoTrackBinder) service)
						.getService();
			}

			public void onServiceDisconnected(ComponentName className) {
				geoTrackService = null;
			}
		};
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
			LocationManager locMgr = (LocationManager) getContext()
					.getSystemService(Context.LOCATION_SERVICE);
			if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				startRecording();
			} else {
				// we can't turn GPS on directly, the best we can do is launch
				// the settings page
				ViewUtil.showGPSDialog(getContext());
			}
		} else {
			notifyQuestionListeners(QuestionInteractionEvent.END_TRACK);
			trackButton.setText(R.string.starttrack);
			isRunning = false;
			ArrayList<String> points = geoTrackService.getPoints();
			if (points != null) {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < points.size(); i++) {
					builder.append(points.get(i)).append(" ");
				}
				if (getResponse() != null) {
					getResponse().setValue(builder.toString().trim());
				} else {
					setResponse(new QuestionResponse(builder.toString().trim(),
							ConstantUtil.TRACK_RESPONSE_TYPE, getQuestion()
									.getId()));
				}
			}
			Intent i = new Intent(getContext(), GeoTrackService.class);
			getContext().unbindService(connection);
			getContext().getApplicationContext().stopService(i);

		}
	}

	private void startRecording() {
		notifyQuestionListeners(QuestionInteractionEvent.START_TRACK);
		trackButton.setText(R.string.endtrack);
		isRunning = true;
		Intent i = new Intent(getContext(), GeoTrackService.class);
		getContext().bindService(new Intent(i), connection,
				Context.BIND_AUTO_CREATE);
		getContext().startService(i);
	}
}
