package com.gallatinsystems.framework.dao;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;

import com.gallatinsystems.framework.domain.BaseDomain;

public abstract class BaseDAO<T extends BaseDomain> {
	protected PersistenceManager pm;	
	private Class<T> concreteClass;

	/**
	 * Injected version of the actual Class to pass for the persistentClass in
	 * the query creation. This must be set before using this implementation
	 * class or any derived class.
	 * 
	 * @param e
	 *            an instance of the type of object to use for this instance of
	 *            the DAO implementation.
	 */
	public void setDomainClass(Class<T> e) {
		this.concreteClass = e;
	}

	private void init() {
		pm = PMF.get().getPersistenceManager();
	}

	public BaseDAO() {
		init();
	}

	public PersistenceManager getPersistenceManager() {
		if (pm == null) {
			init();
		}
		return pm;
	}

	public <E extends BaseDomain> E save(E obj) {
		if (obj.getCreatedDateTime() == null) {
			obj.setCreatedDateTime(new Date());
		}
		if (obj.getLastUpdateDateTime() == null) {
			obj.setLastUpdateDateTime(new Date());
		}
		return pm.makePersistent(obj);
	}

	public T saveOrUpdate(T object) {
		return save(object);
	}

	@SuppressWarnings("unchecked")
	public T getByKey(Long keyId) {
		return (T) pm.getObjectById(keyId);
	}

	@SuppressWarnings("unchecked")
	public List<T> list() {
		javax.jdo.Query query = pm.newQuery(concreteClass);
		List<T> results = (List<T>) query.execute();
		return results;
	}

	@SuppressWarnings("unchecked")
	public <E extends BaseDomain> List<E> list(Class<E> c) {
		javax.jdo.Query query = pm.newQuery(c);
		List<E> results = (List<E>) query.execute();
		return results;
	}

}
