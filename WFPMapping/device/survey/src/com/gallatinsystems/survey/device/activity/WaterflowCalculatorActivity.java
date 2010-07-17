package com.gallatinsystems.survey.device.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.gallatinsystems.survey.device.R;

/**
 * this activity is used to calculate water flow. It presents 3 fields (pipe
 * diameter, water velocity and flow rate). The user can fill in any 2 fields
 * and press the "Solve" button to get the value for the third.
 * 
 * @author Christopher Fagiani
 * 
 */
public class WaterflowCalculatorActivity extends Activity implements
		OnClickListener {

	private Button solveButton;
	private EditText diameterText;
	private EditText velocityText;
	private EditText flowRateText;
	private Spinner diaMetricSpinner;
	private Spinner velocityMetricSpinner;
	private Spinner flowRateMetricSpinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waterflowcalc);
		solveButton = (Button) findViewById(R.id.solvebutton);
		diameterText = (EditText) findViewById(R.id.diaedit);
		velocityText = (EditText) findViewById(R.id.velocityedit);
		flowRateText = (EditText) findViewById(R.id.flowrateedit);
		diaMetricSpinner = (Spinner) findViewById(R.id.diametricspinner);
		velocityMetricSpinner = (Spinner) findViewById(R.id.velocitymetricspinner);
		flowRateMetricSpinner = (Spinner) findViewById(R.id.flowratemetricspinner);

		initializeViews();
	}

	/**
	 * loads options into the spinners
	 */
	private void initializeViews() {
		diaMetricSpinner.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.lengthmetric,
				android.R.layout.simple_spinner_dropdown_item));		
		velocityMetricSpinner.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.velocitymetric,
				android.R.layout.simple_spinner_dropdown_item));
		flowRateMetricSpinner.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.flowratemetric,
				android.R.layout.simple_spinner_dropdown_item));
		solveButton.setOnClickListener(this);
	}

	/**
	 * solves the equation and displays the results
	 */
	@Override
	public void onClick(View v) {

	}

}
