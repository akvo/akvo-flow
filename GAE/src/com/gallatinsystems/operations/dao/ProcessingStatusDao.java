package com.gallatinsystems.operations.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.operations.domain.ProcessingStatus;

/**
 * data access object for the processing status domain
 * 
 * @author Christopher Fagiani
 * 
 */
public class ProcessingStatusDao extends BaseDAO<ProcessingStatus> {

	public ProcessingStatusDao() {
		super(ProcessingStatus.class);
	}

	/**
	 * returns a single ProcessingStatus object using its code
	 * 
	 * @param code
	 * @return
	 */
	public ProcessingStatus getStatusByCode(String code) {
		return findByProperty("code", code, "String");
	}

}
