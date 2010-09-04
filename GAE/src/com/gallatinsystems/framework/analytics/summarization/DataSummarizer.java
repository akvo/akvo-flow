package com.gallatinsystems.framework.analytics.summarization;

/**
 * Classes that implement this interface will perform some sort of data
 * summarization and (most likely) persist the result.
 * 
 * @author Christopher Fagiani
 * 
 */
public interface DataSummarizer {

	public static final int BATCH_SIZE = 10;

	/**
	 * perform and store the summarization.
	 * 
	 * @param key
	 * @param type
	 * @param value
	 *            - payload
	 * @param offset
	 *            - integer offset used for batching processing. This offset is
	 *            used for batching at the input object level.
	 * @param cursor
	 *            - used for batching based on results from the data store
	 * @return - true if summarization completed, false if not
	 */
	public boolean performSummarization(String key, String type, String value,
			Integer offset, String cursor);

	/**
	 * returns the current cursor string, if any
	 * 
	 * @return
	 */
	public String getCursor();
}
