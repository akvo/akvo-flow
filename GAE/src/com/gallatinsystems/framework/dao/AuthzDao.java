package com.gallatinsystems.framework.dao;

import java.util.List;
import java.util.Set;

import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.User;

public interface AuthzDao {
    AllowedResponse getAllowedObjects(Long flowUserId);

    boolean hasPermInTree(List<Long> objectIds, Long userId, Permission permission);

    String getPermissionsMap(User currentUser);

    User findUserByEmail(String email);

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
