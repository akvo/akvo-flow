package com.gallatinsystems.framework.analytics.summarization;

/**
 * Classes that implement this interface will perform some sort of data
 * summarization and (most likely) persist the result.
 * 
 * @author Christopher Fagiani
 * 
 */
public interface DataSummarizer {

	/**
	 * perform and store the summarization.
	 * 
	 * @param key
	 * @param type
	 * @return - true if summarization succeeded, false if not
	 */
	public boolean performSummarization(String key, String type);

}
