package com.gallatinsystems.survey.device.exception;

/**
 * exception class that allows for capturing of survey/instance ids
 * 
 * @author Christopher Fagiani
 * 
 */
public class TransferException extends Exception {

	private static final long serialVersionUID = -4649864250226025982L;
	private String surveyId;
	private Long instanceId;

	public TransferException(Exception e) {
		super(e);
	}

	public TransferException(String surveyId, Long instId, Exception cause) {
		super(cause);
		this.surveyId = surveyId;
		this.instanceId = instId;
	}

	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		if (surveyId != null) {
			builder.append("Survey id: ").append(surveyId)
					.append("\n");
		}
		if (instanceId != null) {
			builder.append("Instance id: ").append(instanceId.toString())
					.append("\n");
		}
		builder.append(super.getMessage());
		return builder.toString();
	}

}
