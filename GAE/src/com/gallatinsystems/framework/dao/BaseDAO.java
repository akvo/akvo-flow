package com.gallatinsystems.framework.dao;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;

public class BaseDAO {
	PersistenceManager pm;
	private void init() {
		pm = PMF.get().getPersistenceManager();
	}
	
	public BaseDAO(){
		init();
	}
	
	public PersistenceManager getPersistenceManager(){
		return pm;
	}
	

}
