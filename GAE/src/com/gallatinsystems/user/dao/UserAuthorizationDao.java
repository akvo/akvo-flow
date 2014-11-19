
package com.gallatinsystems.user.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.user.domain.UserAuthorization;

public class UserAuthorizationDao extends BaseDAO<UserAuthorization> {

    public UserAuthorizationDao() {
        super(UserAuthorization.class);
    }

    /**
     * List the user authorizations that correspond to a specific object path or set of paths
     *
     * @param userId
     * @param objectPath
     * @return
     */
    public List<UserAuthorization> listByObjectPath(Long userId, String objectPath) {
        PersistenceManager pm = PersistenceFilter.getManager();
        String queryString = "userId == :p1 && :p2.contains(objectPath)";
        javax.jdo.Query query = pm.newQuery(UserAuthorization.class, queryString);
        List<UserAuthorization> results = (List<UserAuthorization>) query.execute(userId,
                objectPath);
        return results;
    }

    /**
     * List the user authorizations that correspond to specific roles of a specific user
     *
     * @param userId
     * @param roleId
     * @return
     */
    public List<UserAuthorization> listByUserRole(Long userId, Long roleId) {
        return null;
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
