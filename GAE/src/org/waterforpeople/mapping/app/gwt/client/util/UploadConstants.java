package org.waterforpeople.mapping.app.gwt.client.util;

import com.google.gwt.i18n.client.Constants;

public interface UploadConstants extends Constants {

	public String uploadUrl();

	public String s3Id();

	public String surveyDataS3Policy();

	public String surveyDataS3Sig();

	public String surveyDataS3Path();

	public String surveyDataContentType();

	public String imageS3Policy();

	public String imageS3Sig();

	public String imageS3Path();

	public String imageContentType();

	public String helpS3Policy();

	public String helpS3Sig();

	public String helpS3Path();

	public String videoContentType();

}
