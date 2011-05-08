package com.gallatinsystems.messaging.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.messaging.domain.Message;

public class MessageDao extends BaseDAO<Message> {

	public MessageDao() {
		super(Message.class);
		// TODO Auto-generated constructor stub
	}

	public List<Message> listBySubjectandId(String about, Long id) {
		return null;
	}

}
