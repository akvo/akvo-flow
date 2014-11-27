
package com.gallatinsystems.user.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
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
        List<String> paths = listParentPaths(objectPath);
        PersistenceManager pm = PersistenceFilter.getManager();
        String queryString = "userId == :p1 && :p2.contains(objectPath)";
        javax.jdo.Query query = pm.newQuery(UserAuthorization.class, queryString);
        List<UserAuthorization> results = (List<UserAuthorization>) query.execute(userId, paths);
        return results;
    }

    /**
     * Split the path of an object into a list of the paths of all its parent objects
     *
     * @param objectPath
     * @return
     */
    private List<String> listParentPaths(String objectPath) {
        List<String> parentPaths = new ArrayList<String>();
        StringBuilder path = new StringBuilder(objectPath);
        while (path.length() > 1) {
            path.delete(path.lastIndexOf("/"), path.length());
            parentPaths.add(path.toString().trim());
        }
        parentPaths.set(parentPaths.size() - 1, "/"); // set last element to root path

        return parentPaths;
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
     * @param objectPath
     * @return
     */
    @SuppressWarnings("unchecked")
    public UserAuthorization findUserAuthorization(Long userId, Long roleId, String objectPath) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(UserAuthorization.class);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        ;
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();

        appendNonNullParam("userId", filterString, paramString, "Long", userId,
                paramMap);
        appendNonNullParam("roleId", filterString, paramString,
                "Long", roleId, paramMap);
        appendNonNullParam("objectPath", filterString, paramString,
                "String", objectPath, paramMap);

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
