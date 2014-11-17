
package com.gallatinsystems.user.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.user.domain.UserAuthorization;

public class UserAuthorizationDao extends BaseDAO<UserAuthorization> {

    public UserAuthorizationDao() {
        super(UserAuthorization.class);
    }

    /**
     * List the user authorizations that correspond to a specific object path or set of paths
     *
     * @param userId
     * @param roleId
     * @param objectPath
     * @return
     */
    public List<UserAuthorization> listByObjectPath(Long userId, Long roleId, String objectPath) {
        return null;
    }

    /**
     * List the user authorizations that correspond to specific roles of a specific user
     *
     * @param userId
     * @param roleId
     * @return
     */
    public List<UserAuthorization> listByUserRoles(Long userId, Long roleId) {
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
}
