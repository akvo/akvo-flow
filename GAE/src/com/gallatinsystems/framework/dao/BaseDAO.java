package com.gallatinsystems.framework.dao;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class BaseDAO<T extends BaseDomain> {
	private Class<T> concreteClass;

	public BaseDAO(Class<T> e) {
		setDomainClass(e);
	}

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

	/**
	 * saves an object to the data store AND closes the persistence manager
	 * instance to force a flush
	 * 
	 * @param <E>
	 * @param obj
	 * @return
	 */
	public <E extends BaseDomain> E save(E obj) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		if (obj.getCreatedDateTime() == null) {
			obj.setCreatedDateTime(new Date());
		}
		if (obj.getLastUpdateDateTime() == null) {
			obj.setLastUpdateDateTime(new Date());
		}
		try {
			obj = pm.makePersistent(obj);
		} finally {
			pm.close();
		}

		return obj;
	}

	public T saveOrUpdate(T object) {
		return save(object);
	}

	/**
	 * gets the core persistent object for the dao concrete class using the
	 * string key (obtained from KeyFactory.stringFromKey())
	 * 
	 * @param keyString
	 * @return
	 */
	public T getByKey(String keyString) {
		return getByKey(keyString, concreteClass);
	}

	/**
	 * convenience method to allow loading of other persistent objects by key
	 * from this dao
	 * 
	 * @param keyString
	 * @return
	 */
	public <E extends BaseDomain> E getByKey(String keyString, Class<E> clazz) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		E result = null;

		Key k = KeyFactory.stringToKey(keyString);
		result = (E) pm.getObjectById(clazz, k);
		return result;
	}

	/**
	 * lists all of the concreteClass instances in the datastore. TODO: if we
	 * think we'll use this on large tables, we should use Extents
	 * 
	 * @return
	 */
	public List<T> list() {
		return list(concreteClass);
	}

	/**
	 * lists all of the type passed in.
	 * 
	 * TODO: if we think we'll use this on large tables, we should use Extents
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E extends BaseDomain> List<E> list(Class<E> c) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<E> results = null;
		javax.jdo.Query query = pm.newQuery(c);
		results = (List<E>) query.execute();

		return results;
	}

	/**
	 * returns a single object based on the property value
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param propertyType
	 * @return
	 */
	protected T findByProperty(String propertyName, Object propertyValue,
			String propertyType) {
		T result = null;
		List<T> results = listByProperty(propertyName, propertyValue,
				propertyType);
		if (results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/**
	 * lists all the objects of the same type as the concreteClass with property
	 * equal to the value passed in
	 * 
	 * since using this requires the caller know the persistence data type of
	 * the field and the field name, this method is protected so that it can
	 * only be used by subclass DAOs. We don't want those details to leak into
	 * higher layers of the code.
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param propertyType
	 * @return
	 */
	protected List<T> listByProperty(String propertyName, Object propertyValue,
			String propertyType) {
		return listByProperty(propertyName, propertyValue, propertyType,
				concreteClass);
	}

	/**
	 * convenience method to list all instances of the type passed in that match
	 * the property
	 * 
	 * since using this requires the caller know the persistence data type of
	 * the field and the field name, this method is protected so that it can
	 * only be used by subclass DAOs. We don't want those details to leak into
	 * higher layers of the code.
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param propertyType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <E extends BaseDomain> List<E> listByProperty(
			String propertyName, Object propertyValue, String propertyType,
			Class<E> clazz) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<E> results = null;

		javax.jdo.Query query = pm.newQuery(clazz);
		query.setFilter(propertyName + " == " + propertyName + "Param");
		query.declareParameters(propertyType + " " + propertyName + "Param");
		results = (List<E>) query.execute(propertyValue);

		return results;
	}
}
