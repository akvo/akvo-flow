package com.gallatinsystems.framework.dao;

import java.util.List;

import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.User;

public interface AuthzDao {
    AllowedResponse getAllowedObjects(Long flowUserId);

    boolean hasPermInTree(List<Long> objectIds, Long userId, Permission permission);

    String getPermissionsMap(User currentUser);

    User findUserByEmail(String email);

}
