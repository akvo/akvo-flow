package com.gallatinsystems.sms.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.sms.domain.SMSMessage;

/**
 * dao to save/find incoming SMS messages
 * 
 * @author Christopher Fagiani
 * 
 */
public class SMSMessageDao extends BaseDAO<SMSMessage> {

	public SMSMessageDao() {
		super(SMSMessage.class);
	}
}
