package com.gallatinsystems.framework.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.akvo.flow.util.FlowJsonObjectReader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.utils.SystemProperty;

public class HttpAuthzDao implements AuthzDao {

    private static final TypeReference<AllowedResponse> TYPE_REFERENCE = new TypeReference<AllowedResponse>() {
    };

    @Override
    public AllowedResponse getAllowedObjects(Long flowUserId) {
        try {
            URL url = new URL("https://authz-auth0.akvotest.org/authz/poc?" + "flowUserId=" + flowUserId + "&" + "flowInstance="
                    + SystemProperty.applicationId.get());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            String s = builder.toString();
            FlowJsonObjectReader flowJsonObjectReader = new FlowJsonObjectReader();
            return flowJsonObjectReader.readObject(s, TYPE_REFERENCE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasPermInTree(List<Long> objectIds, Long userId, Permission permission) {
        return false;
    }

    @Override
    public String getPermissionsMap(User currentUser) {
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

}
