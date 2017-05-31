/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.framework.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import net.sf.jsr107cache.CacheException;

import org.akvo.flow.domain.SecuredObject;
import org.datanucleus.store.appengine.query.JDOCursorHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.user.dao.UserAuthorizationDAO;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;
import com.gallatinsystems.user.domain.UserAuthorization;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * This is a reusable data access object that supports basic operations (save, find by property,
 * list).
 *
 * @author Christopher Fagiani
 * @param <T> a persistent class that extends BaseDomain
 */
public class BaseDAO<T extends BaseDomain> {
    public static final int DEFAULT_RESULT_COUNT = 20;
    protected static final int RETRY_INTERVAL_MILLIS = 200;
    protected static final String STRING_TYPE = "String";
    protected static final String NOT_EQ_OP = "!=";
    protected static final String EQ_OP = " == ";
    protected static final String GTE_OP = " >= ";
    protected static final String LTE_OP = " <= ";
    private Class<T> concreteClass;
    protected Logger log;

    public enum CURSOR_TYPE {
        all
    };

    public BaseDAO(Class<T> e) {
        setDomainClass(e);
        log = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Injected version of the actual Class to pass for the persistentClass in the query creation.
     * This must be set before using this implementation class or any derived class.
     *
     * @param e an instance of the type of object to use for this instance of the DAO
     *            implementation.
     */
    public void setDomainClass(Class<T> e) {
        this.concreteClass = e;
    }

    /**
     * saves an object to the data store. This method will set the lastUpdateDateTime on the domain
     * object prior to saving and will set the createdDateTime (if it is null).
     *
     * @param <E>
     * @param obj
     * @return
     */
    public <E extends BaseDomain> E save(E obj) {
        PersistenceManager pm = PersistenceFilter.getManager();
        obj.setLastUpdateDateTime(new Date());
        if (obj.getCreatedDateTime() == null) {
            obj.setCreatedDateTime(obj.getLastUpdateDateTime());
        }
        obj = pm.makePersistent(obj);
        return obj;
    }

    /**
     * saves all instances contained within the collection passed in. This will set the
     * lastUpdateDateTime for the objects prior to saving.
     *
     * @param <E>
     * @param objList
     * @return
     */
    public <E extends BaseDomain> Collection<E> save(Collection<E> objList) {
        if (objList != null) {
            for (E item : objList) {
                item.setLastUpdateDateTime(new Date());
                if (item.getCreatedDateTime() == null) {
                    item.setCreatedDateTime(item.getLastUpdateDateTime());
                }
            }
            PersistenceManager pm = PersistenceFilter.getManager();
            objList = pm.makePersistentAll(objList);
        }
        return objList;
    }

    /**
     * gets the core persistent object for the dao concrete class using the string key (obtained
     * from KeyFactory.stringFromKey())
     *
     * @param keyString
     * @return
     */
    public T getByKey(String keyString) {
        return getByKey(keyString, concreteClass);
    }

    /**
     * gets an object by key
     *
     * @param key
     * @return
     */
    public T getByKey(Key key) {
        return getByKey(key, concreteClass);
    }

    /**
     * convenience method to allow loading of other persistent objects by key from this dao
     *
     * @param keyString
     * @return
     */
    public <E extends BaseDomain> E getByKey(String keyString, Class<E> clazz) {
        PersistenceManager pm = PersistenceFilter.getManager();
        E result = null;
        Key k = KeyFactory.stringToKey(keyString);
        try {
            result = pm.getObjectById(clazz, k);
        } catch (JDOObjectNotFoundException nfe) {
            log.warning("No " + clazz.getCanonicalName() + " found with key: "
                    + k);
        }
        return result;
    }

    /**
     * gets a single object identified by the key passed in.
     *
     * @param <E>
     * @param key
     * @param clazz
     * @return the object corresponding to the key (or null if not found)
     */
    public <E extends BaseDomain> E getByKey(Key key, Class<E> clazz) {
        PersistenceManager pm = PersistenceFilter.getManager();
        E result = null;

        try {
            result = pm.getObjectById(clazz, key);
        } catch (JDOObjectNotFoundException nfe) {
            log.warning("No " + clazz.getCanonicalName() + " found with key: "
                    + key);
        }
        return result;
    }

    /**
     * gets a single object by key where the key is represented as a Long
     *
     * @param id
     * @return
     */
    public T getByKey(Long id) {
        return getByKey(id, concreteClass);
    }

    /**
     * gets a single object by key where the key is represented as a Long and the type is the class
     * passed in via clazz
     *
     * @param <E>
     * @param id
     * @param clazz
     * @return
     */
    public <E extends BaseDomain> E getByKey(Long id, Class<E> clazz) {
        PersistenceManager pm = PersistenceFilter.getManager();
        String itemKey = KeyFactory.createKeyString(clazz.getSimpleName(), id);
        E result = null;
        try {
            result = pm.getObjectById(clazz, itemKey);
        } catch (JDOObjectNotFoundException nfe) {
            log.warning("No " + clazz.getCanonicalName() + " found with id: "
                    + id);
        }
        return result;
    }

    /**
     * lists all of the concreteClass instances in the datastore, using a page size.
     *
     * @return
     */
    public List<T> list(String cursorString, Integer pageSize) {
        return list(concreteClass, cursorString, pageSize);
    }

    /**
     * lists all of the concreteClass instances in the datastore. if we think we'll use this on
     * large tables, we should use Extents
     *
     * @return
     */
    public List<T> list(String cursorString) {
        return list(concreteClass, cursorString);
    }

    /**
     * Lists all of the concreteClass instances in the datastore
     */
    public <E extends BaseDomain> List<E> list(Class<E> c, String cursorString) {
        return list(c, cursorString, null);
    }

    /**
     * lists all of the type passed in. if we think we'll use this on large tables, we should use
     * Extents
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E extends BaseDomain> List<E> list(Class<E> c, String cursorString, Integer pageSize) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(c);

        if (cursorString != null
                && !cursorString.trim().toLowerCase()
                        .equals(Constants.ALL_RESULTS)) {
            Cursor cursor = Cursor.fromWebSafeString(cursorString);
            Map<String, Object> extensionMap = new HashMap<String, Object>();
            extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
            query.setExtensions(extensionMap);
        }

        List<E> results = null;

        if (pageSize == null) {
            this.prepareCursor(cursorString, query);
        } else {
            this.prepareCursor(cursorString, pageSize, query);
        }

        results = (List<E>) query.execute();
        return results;
    }

    /**
     * Return a list of survey groups or surveys that are accessible by the current user, filtered
     * by object ids
     *
     * @return
     */
    public <E extends BaseDomain> List<E> filterByUserAuthorizationObjectId(List<E> allObjectsList,
            Long userId) {
        if (!concreteClass.isAssignableFrom(SurveyGroup.class)
                && !concreteClass.isAssignableFrom(Survey.class)) {
            throw new UnsupportedOperationException("Cannot filter "
                    + concreteClass.getSimpleName());
        }

        UserDao userDAO = new UserDao();
        User user = userDAO.getByKey(userId);
        if (user.isSuperAdmin()) {
            return allObjectsList;
        }

        UserAuthorizationDAO userAuthorizationDAO = new UserAuthorizationDAO();
        List<UserAuthorization> userAuthorizationList = userAuthorizationDAO.listByUser(userId);
        if (userAuthorizationList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> securedObjectIds = new HashSet<>();
        for (UserAuthorization auth : userAuthorizationList) {
            if (auth.getSecuredObjectId() != null) {
                securedObjectIds.add(auth.getSecuredObjectId());
            }
        }

        // Set of all ancestor ids of secured objects
        Set<Long> securedAncestorIds = new HashSet<>();
        for (Object obj : allObjectsList) {
            SecuredObject securedObject = (SecuredObject) obj;
            if (securedObjectIds.contains(securedObject.getObjectId())) {
                securedAncestorIds.addAll(securedObject.listAncestorIds());
            }
        }

        Set<E> authorizedSet = new HashSet<>();
        if (concreteClass.isAssignableFrom(SurveyGroup.class)) {
            for (E obj : allObjectsList) {
                SurveyGroup sg = (SurveyGroup) obj;
                Long sgId = sg.getKey().getId();
                if (hasAuthorizedAncestors(sg.getAncestorIds(), securedObjectIds)
                        || securedObjectIds.contains(sgId)
                        || securedAncestorIds.contains(sgId)) {
                    authorizedSet.add(obj);
                }
            }
        } else {
            for (E obj : allObjectsList) {
                Survey s = (Survey) obj;
                List<Long> ancestorIds = s.getAncestorIds();
                if (hasAuthorizedAncestors(ancestorIds, securedObjectIds)) {
                    authorizedSet.add(obj);
                }
            }
        }

        List<E> authorizedList = new ArrayList<>();
        authorizedList.addAll(authorizedSet);

        return authorizedList;
    }

    public <E extends BaseDomain> List<E> filterByUserAuthorizationObjectId(List<E> allObjectsList) {
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        final Long userId = (Long) authentication.getCredentials();
        return filterByUserAuthorizationObjectId(allObjectsList, userId);
    }

    /**
     * Check whether one or more items in the list of an entity's ancestor ids is present in the
     * list of authorized objects for a user. Return true if this is the case
     *
     * @param ancestorIds
     * @param securedObjectIds
     * @return
     */
    private boolean hasAuthorizedAncestors(List<Long> ancestorIds, Set<Long> securedObjectIds) {
        // use new List object to prevent side effects on SurveyGroup.ancestorIds property
        List<Long> idsList = new ArrayList<Long>(ancestorIds);
        return idsList != null && idsList.removeAll(securedObjectIds);
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
     * gets a List of object by key where the key is represented as a Long
     *
     * @param ids Array of Long representing the keys of objects
     * @return null if ids is null, otherwise a list of objects
     */
    public List<T> listByKeys(Long[] ids) {
        if (ids == null) {
            return null;
        }
        final List<T> list = new ArrayList<T>();
        for (Long id : ids) {
            final T obj = getByKey(id);
            if (obj != null) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * Retrieves a List of objects by key where the keys are represented by a Collection of Longs
     *
     * @param ids List of Long representing the keys of objects
     * @return empty list if ids is null, otherwise a list of objects
     */
    public List<T> listByKeys(List<Long> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        final List<T> list = new ArrayList<T>();
        for (Long id : ids) {
            final T obj = getByKey(id);
            if (obj != null) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * lists all the objects of the same type as the concreteClass with property equal to the value
     * passed in since using this requires the caller know the persistence data type of the field
     * and the field name, this method is protected so that it can only be used by subclass DAOs. We
     * don't want those details to leak into higher layers of the code.
     *
     * @param propertyName
     * @param propertyValue
     * @param propertyType
     * @return
     */
    protected List<T> listByProperty(String propertyName, Object propertyValue,
            String propertyType) {
        return listByProperty(propertyName, propertyValue, propertyType, null,
                null, EQ_OP, concreteClass);
    }

    /**
     * lists all objects of type class that have the property name/value passed in
     *
     * @param <E>
     * @param propertyName
     * @param propertyValue
     * @param propertyType
     * @param clazz
     * @return
     */
    protected <E extends BaseDomain> List<E> listByProperty(
            String propertyName, Object propertyValue, String propertyType,
            Class<E> clazz) {
        return listByProperty(propertyName, propertyValue, propertyType, null,
                null, EQ_OP, clazz);
    }

    /**
     * lists all instances of type clazz that have the property equal to the value passed in and
     * orders the results by the field specified. NOTE: for this to work on the datastore, you may
     * need to have an index defined.
     *
     * @param <E>
     * @param propertyName
     * @param propertyValue
     * @param propertyType
     * @param orderBy
     * @param clazz
     * @return
     */
    protected <E extends BaseDomain> List<E> listByProperty(
            String propertyName, Object propertyValue, String propertyType,
            String orderBy, Class<E> clazz) {
        return listByProperty(propertyName, propertyValue, propertyType,
                orderBy, null, EQ_OP, clazz);
    }

    /**
     * lists all instances that have the property name/value matching those passed in optionally
     * sorted by the order by column and direction. NOTE: depending on the sort being done, you may
     * need an index in the datastore for this to work.
     *
     * @param propertyName
     * @param propertyValue
     * @param propertyType
     * @param orderByCol
     * @param orderByDir
     * @return
     */
    protected List<T> listByProperty(String propertyName, Object propertyValue,
            String propertyType, String orderByCol, String orderByDir) {
        return listByProperty(propertyName, propertyValue,
                propertyType, orderByCol, orderByDir, EQ_OP, concreteClass);
    }

    /**
     * lists all instances that have the property name/value matching those passed in optionally
     * sorted by the order by column. NOTE: depending on the sort being done, you may need an index
     * in the datastore for this to work.
     *
     * @param propertyName
     * @param propertyValue
     * @param propertyType
     * @param orderByCol
     * @return
     */
    protected List<T> listByProperty(String propertyName, Object propertyValue,
            String propertyType, String orderByCol) {
        return listByProperty(propertyName, propertyValue, propertyType,
                orderByCol, null, EQ_OP, concreteClass);
    }

    /**
     * convenience method to list all instances of the type passed in that match the property since
     * using this requires the caller know the persistence data type of the field and the field
     * name, this method is protected so that it can only be used by subclass DAOs. We don't want
     * those details to leak into higher layers of the code.
     *
     * @param propertyName
     * @param propertyValue
     * @param propertyType
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <E extends BaseDomain> List<E> listByProperty(
            String propertyName, Object propertyValue, String propertyType,
            String orderByField, String orderByDir, String operator,
            Class<E> clazz) {
        PersistenceManager pm = PersistenceFilter.getManager();
        List<E> results = null;

        String paramName = propertyName + "Param";
        if (paramName.contains(".")) {
            paramName = paramName.substring(paramName.indexOf(".") + 1);
        }
        javax.jdo.Query query = pm.newQuery(clazz);
        query.setFilter(propertyName + " " + operator + " " + paramName);

        if (orderByField != null) {
            query.setOrdering(orderByField
                    + (orderByDir != null ? " " + orderByDir : ""));
        }
        query.declareParameters(propertyType + " " + paramName);
        if (propertyValue instanceof Date) {
            query.declareImports("import java.util.Date");
        }
        results = (List<E>) query.execute(propertyValue);

        return results;
    }

    /**
     * deletes an object from the db
     *
     * @param <E>
     * @param obj
     */
    public <E extends BaseDomain> void delete(E obj) {
        PersistenceManager pm = PersistenceFilter.getManager();
        pm.deletePersistent(obj);
    }

    /**
     * deletes a list of objects in a single datastore interaction
     */
    public <E extends BaseDomain> void delete(Collection<E> obj) {
        PersistenceManager pm = PersistenceFilter.getManager();
        pm.deletePersistentAll(obj);
    }

    /**
     * utility method to form a hash map of query parameters using an equality operator
     *
     * @param paramName - name of object property
     * @param filter - in/out stringBuilder of query filters
     * @param param -in/out stringBuilder of param names
     * @param type - data type of field
     * @param value - value to bind to param
     * @param paramMap - in/out parameter map
     */
    protected void appendNonNullParam(String paramName, StringBuilder filter,
            StringBuilder param, String type, Object value,
            Map<String, Object> paramMap) {
        appendNonNullParam(paramName, filter, param, type, value, paramMap,
                EQ_OP);
    }

    /**
     * utility method to form a hash map of query parameters
     *
     * @param paramName - name of object property
     * @param filter - in/out stringBuilder of query filters
     * @param param -in/out stringBuilder of param names
     * @param type - data type of field
     * @param value - value to bind to param
     * @param paramMap - in/out parameter map
     * @param operator - operator to use
     */
    protected void appendNonNullParam(String paramName, StringBuilder filter,
            StringBuilder param, String type, Object value,
            Map<String, Object> paramMap, String operator) {
        if (value != null) {
            if (paramMap.keySet().size() > 0) {
                filter.append(" && ");
                param.append(", ");
            }
            String paramValName = paramName + "Param"
                    + paramMap.keySet().size();
            filter.append(paramName).append(" ").append(operator).append(" ")
                    .append(paramValName);
            param.append(type).append(" ").append(paramValName);
            paramMap.put(paramValName, value);
        }
    }

    /**
     * gets a GAE datastore cursor based on the results list passed in. The list must be a non-null
     * list of persistent entities (entites retrived from the datastore in the same session).
     *
     * @param results
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getCursor(List results) {
        if (results != null && results.size() > 0) {
            Cursor cursor = JDOCursorHelper.getCursor(results);
            if (cursor != null) {
                return cursor.toWebSafeString();
            } else {
                return null;
            }
        }
        return null;

    }

    /**
     * sets up the cursor with the given page size (or no page size if the cursor string is set to
     * the ALL_RESULTS constant)
     *
     * @param cursorString
     * @param pageSize
     * @param query
     */
    protected void prepareCursor(String cursorString, Integer pageSize,
            javax.jdo.Query query) {
        if (cursorString != null
                && !cursorString.trim().toLowerCase()
                        .equals(Constants.ALL_RESULTS)) {
            Cursor cursor = Cursor.fromWebSafeString(cursorString);
            Map<String, Object> extensionMap = new HashMap<String, Object>();
            extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
            query.setExtensions(extensionMap);
        }
        if (cursorString == null || !cursorString.equals(Constants.ALL_RESULTS)) {
            if (pageSize == null) {
                query.setRange(0, DEFAULT_RESULT_COUNT);
            } else {
                query.setRange(0, pageSize);
            }
        }
    }

    /**
     * this method should only be used when running a lot of datastore operations in a single
     * request to a task queue and or backend (regular online requests can't accumulate enough data
     * to require a flush without timing out).
     */
    public void flushBatch() {
        PersistenceManager pm = PersistenceFilter.getManager();
        pm.flush();
    }

    /**
     * sets up the cursor using the default page size
     *
     * @param cursorString
     * @param query
     */
    protected void prepareCursor(String cursorString, javax.jdo.Query query) {
        prepareCursor(cursorString, DEFAULT_RESULT_COUNT, query);
    }

    /**
     * method used to sleep in the event of a retry
     */
    protected static void sleep() {
        try {
            Thread.sleep(RETRY_INTERVAL_MILLIS);
        } catch (InterruptedException e) {
            // no-op
        }
    }

    /**
     * Default format for cache key string
     *
     * @param object
     * @return
     * @throws CacheException
     */
    public String getCacheKey(BaseDomain object) throws CacheException {
        if (object.getKey() == null) {
            throw new CacheException("Trying to get cache key from an unsaved object");
        }
        return object.getClass().getSimpleName() + "-" + object.getKey().getId();
    }

    /**
     * Default format for cache key string
     *
     * @param objectId
     * @return
     * @throws CacheException
     */
    public String getCacheKey(String objectId) throws CacheException {
        if (objectId == null) {
            throw new CacheException("Trying to get cache key from an unsaved object");
        }
        return concreteClass.getSimpleName() + "-" + objectId;
    }
}
