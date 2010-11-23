package com.gallatinsystems.survey.device.view.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.FileTransmission;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Adapter that converts FileTransmission objects for display in a list view.
 * 
 * @author Christopher Fagiani
 * 
 */
public class FileTransmissionArrayAdapter extends
		ArrayAdapter<FileTransmission> {

	private DateFormat dateFormat;
	private int layoutId;

	public FileTransmissionArrayAdapter(Context context, int resourceId,
			List<FileTransmission> objects) {
		super(context, resourceId, objects);
		layoutId = resourceId;
		dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	}

	private void bindView(View view, FileTransmission trans) {
		ImageView imageView = (ImageView) view.findViewById(R.id.statusicon);
		if (ConstantUtil.IN_PROGRESS_STATUS.equals(trans.getStatus())) {
			imageView.setImageResource(R.drawable.blueuparrow);
		} else if (ConstantUtil.QUEUED_STATUS.equals(trans.getStatus())) {
			imageView.setImageResource(R.drawable.yellowcircle);
		} else if (ConstantUtil.COMPLETE_STATUS.equals(trans.getStatus())) {
			imageView.setImageResource(R.drawable.greencircle);
		} else if (ConstantUtil.FAILED_STATUS.equals(trans.getStatus())) {
			imageView.setImageResource(R.drawable.redcircle);
		}
		TextView statusLabel = (TextView) view.findViewById(R.id.statustext);
		statusLabel.setText(trans.getStatus());
		TextView startDate = (TextView) view.findViewById(R.id.startdate);
		if (trans.getStartDate() != null) {
			startDate.setText(dateFormat.format(trans.getStartDate()));
		}
		TextView endDate = (TextView) view.findViewById(R.id.enddate);
		if (trans.getEndDate() != null) {
			endDate.setText(dateFormat.format(trans.getEndDate()));
		}

		TextView fileName = (TextView) view.findViewById(R.id.filename);
		fileName.setText(trans.getFileName());

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Context ctx = getContext();
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(layoutId, null);
		bindView(view, getItem(position));
		return view;
	}
}
