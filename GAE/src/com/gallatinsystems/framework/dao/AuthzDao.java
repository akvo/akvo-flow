package com.gallatinsystems.framework.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.akvo.flow.util.FlowJsonObjectWriter;
import org.springframework.security.access.AccessDeniedException;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.user.dao.UserAuthorizationDAO;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.dao.UserRoleDao;
import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.User;
import com.gallatinsystems.user.domain.UserAuthorization;
import com.gallatinsystems.user.domain.UserRole;

public class AuthzDao {

    public AllowedResponse getAllowedObjects(Long flowUserId) {

        UserDao userDAO = new UserDao();
        User user = userDAO.getByKey(flowUserId);

        if (user.isSuperAdmin()) {
            return new AllowedResponse(true);
        }

        UserAuthorizationDAO userAuthorizationDAO = new UserAuthorizationDAO();
        List<UserAuthorization> userAuthorizationList = userAuthorizationDAO.listByUser(flowUserId);

        Set<Long> securedObjectIds = new HashSet<>();
        for (UserAuthorization auth : userAuthorizationList) {
            if (auth.getSecuredObjectId() != null) {
                securedObjectIds.add(auth.getSecuredObjectId());
            }
        }
        return new AllowedResponse(securedObjectIds);
    }

    public boolean hasPermInTree(List<Long> objectIds, Long userId, Permission permission) {
            List<UserAuthorization> authorizations = new UserAuthorizationDAO().listByObjectIds(userId, // merge with call bellow
                    objectIds);
            if (authorizations.isEmpty()) {
                throw new AccessDeniedException("Access is Denied. Insufficient permissions");
            }

            List<Long> authorizedRoleIds = new ArrayList<Long>();
            for (UserAuthorization auth : authorizations) {
                authorizedRoleIds.add(auth.getRoleId());
            }

            List<UserRole> authorizedRoles = new UserRoleDao().listByKeys(authorizedRoleIds // this one to be merged with call above
                    .toArray(new Long[0]));

            for (UserRole role : authorizedRoles) {
                if (role.getPermissions().contains(permission)) {
                    return true;
                }
            }
            return false;

    }

    /**
     * Retrieve a javascript map of the paths and corresponding permissions for the current user
     *
     * @param currentUser
     * @return
     */
    public String getPermissionsMap(User currentUser) {
        // move the whole method
        List<UserAuthorization> authorizationList = new UserAuthorizationDAO().listByUser(currentUser // OK
                .getKey().getId());
        Map<Long, UserRole> roleMap = new HashMap<Long, UserRole>();
        for (UserRole role : new UserRoleDao().list(Constants.ALL_RESULTS)) { // OK
            roleMap.put(role.getKey().getId(), role);
        }
        Map<Long, Set<Permission>> permissions = new HashMap<Long, Set<Permission>>();
        for (UserAuthorization auth : authorizationList) {
            UserRole role = roleMap.get(auth.getRoleId());
            if (role != null && auth.getSecuredObjectId() != null) {
                if (permissions.containsKey(auth.getSecuredObjectId())) {
                    permissions.get(auth.getSecuredObjectId()).addAll(role.getPermissions());
                } else {
                    permissions.put(auth.getSecuredObjectId(), role.getPermissions());
                }
            }
        }

        addSuperAdminPermissions(currentUser, permissions);

        FlowJsonObjectWriter writer = new FlowJsonObjectWriter();
        String permissionsString = null;
        try {
            permissionsString = writer.writeAsString(permissions);
        } catch (IOException e) {
            // ignore
        }

        return permissionsString;
    }

    /**
     * Enable users designated as superAdmin in the backend complete access to all functionality on
     * the frontend
     *
     * @param currentUser
     * @param permissions
     */
    private void addSuperAdminPermissions(User currentUser, Map<Long, Set<Permission>> permissions) {
        if (!currentUser.getPermissionList().equals("0")) {
            return;
        }

        List<Permission> permissionList = Arrays.asList(Permission.values());
        permissions.put(Constants.ROOT_FOLDER_ID, new HashSet<Permission>(permissionList));
    }

    public User findUserByEmail(String email) {
        return new UserDao().findUserByEmail(email);
    }

    public static class AllowedResponse {
        final Set<Long> securedObjectIds;
        final boolean isSuperAdmin;

        public AllowedResponse(boolean superAdmin) {
            this.isSuperAdmin = superAdmin;
            this.securedObjectIds = null;
        }

        public AllowedResponse(Set<Long> securedObjectIds) {
            this.isSuperAdmin = false;
            this.securedObjectIds = securedObjectIds;
        }
    }
}
