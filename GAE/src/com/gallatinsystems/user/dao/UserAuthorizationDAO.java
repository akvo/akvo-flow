
package com.gallatinsystems.user.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.user.domain.UserAuthorization;

public class UserAuthorizationDAO extends BaseDAO<UserAuthorization> {

    public UserAuthorizationDAO() {
        super(UserAuthorization.class);
    }

    /**
     * List the user authorizations that correspond to a specific object path or set of paths
     *
     * @param userId
     * @param objectPath
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserAuthorization> listByObjectPath(Long userId, String objectPath) {
        if (objectPath == null) {
            return Collections.emptyList();
        }
        List<String> paths = SurveyUtils.listParentPaths(objectPath, true);
        paths.add(objectPath); // include path of the object when checking for authorizations
        PersistenceManager pm = PersistenceFilter.getManager();
        String queryString = "userId == :p1 && :p2.contains(objectPath)";
        javax.jdo.Query query = pm.newQuery(UserAuthorization.class, queryString);
        List<UserAuthorization> results = (List<UserAuthorization>) query.execute(userId, paths);
        return results;
    }

    /**
     * List the user authorizations
     *
     * @param userId
     * @param ancestorIds
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserAuthorization> listByObjectIds(Long userId, List<Long> ancestorIds) {
        if (ancestorIds == null || ancestorIds.isEmpty()) {
            return Collections.emptyList();
        }
        PersistenceManager pm = PersistenceFilter.getManager();
        String queryString = "userId == :p1 && :p2.contains(securedObjectId)";
        javax.jdo.Query query = pm.newQuery(UserAuthorization.class, queryString);
        List<UserAuthorization> results = (List<UserAuthorization>) query.execute(userId,
                ancestorIds);
        return results;
    }

    /**
     * List the user authorizations that correspond to a specific user
     *
     * @param userId
     * @return
     */
    public List<UserAuthorization> listByUser(Long userId) {
        return listByProperty("userId", userId, "Long");
    }

    /**
     * Retrieve a specific UserAuthorization entity
     *
     * @param userId
     * @param roleId
     * @param secureObjectId
     * @return
     */
    @SuppressWarnings("unchecked")
    public UserAuthorization findUserAuthorization(Long userId, Long roleId, Long secureObjectId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(UserAuthorization.class);

        Map<String, Object> paramMap = new HashMap<String, Object>();

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();

        appendNonNullParam("userId", filterString, paramString, "Long", userId,
                paramMap);
        appendNonNullParam("roleId", filterString, paramString,
                "Long", roleId, paramMap);
        appendNonNullParam("securedObjectId", filterString, paramString,
                "Long", secureObjectId, paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        List<UserAuthorization> authList = (List<UserAuthorization>) query.executeWithMap(paramMap);
        if (authList.isEmpty()) {
            return null;
        } else {
            return authList.get(0);
        }
    }

    /**
     * Find a list of UserAuthorization objects that contains the given roleId. This is used to test
     * that the role has been assigned regardless of which user it has been assigned to.
     *
     * @param roleId
     * @return
     */
    public List<UserAuthorization> findFirstAssignedByRole(Long roleId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(UserAuthorization.class);
        query.setFilter("roleId == roleIdParam");
        query.declareParameters("Long roleIdParam");
        query.setRange(0, 1);

        return (List<UserAuthorization>) query.execute(roleId);
    }

    /**
     * Wrapper for BaseDAO.save()
     *
     * @param auth
     * @return
     */
    public UserAuthorization save(UserAuthorization auth) {
        return super.save(auth);
    }

    /**
     * Wrapper for BaseDAO.delete()
     *
     * @param auth
     */
    public void delete(UserAuthorization auth) {
        super.delete(auth);
    }
}
