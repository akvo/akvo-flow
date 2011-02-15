package com.gallatinsystems.survey.device.domain;

import android.util.Log;

import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * rule used to translate a free text response into some other type of value.
 * 
 * @author Christopher Fagiani
 * 
 */
public class ScoringRule {

	private static final String TAG = "ScoringRule";

	private String type;
	private String min;
	private String max;
	private String value;
	private String text;

	public ScoringRule(String type, String min, String max, String text,
			String val) {
		this.type = type;
		this.min = min;
		this.max = max;
		this.value = val;
		this.text = text;
		if (type == null) {
			this.type = ConstantUtil.NUMERIC_SCORING;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	/**
	 * attempts to translate the response passed in into a scored value. If the
	 * response does not fall within the min/max range designated on this rule,
	 * this method will return null.
	 * 
	 * rules are considered satisfied if min <= value <= max or, if textmatch,
	 * if text matches response
	 * 
	 * @param response
	 * @return
	 */
	public String scoreResponse(String response) {
		String score = null;
		if (response != null) {
			if (ConstantUtil.NUMERIC_SCORING.equalsIgnoreCase(type)) {
				try {
					Double responseNum = Double.parseDouble(response.trim());
					if (min == null || responseNum >= Double.parseDouble(min)) {
						if (max == null
								|| responseNum <= Double.parseDouble(max)) {
							score = value;
						}
					}
				} catch (NumberFormatException e) {
					Log.e(TAG, "Can't perform numeric scoring", e);
				}
			} else if (ConstantUtil.TEXT_MATCH_SCORING.equalsIgnoreCase(type)) {
				if (response.equals(text)) {
					score = value;
				}
			}
		}
		return score;
	}
}
