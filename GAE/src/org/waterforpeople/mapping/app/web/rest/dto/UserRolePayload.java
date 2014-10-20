
package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.gallatinsystems.user.domain.UserRole;

/**
 * Payload class to wrap the UserRole object. Enables hiding of the internal structure of the
 * UserRole object as well as aggregating data from multiple sources.
 *
 * @author emmanuel
 */
public class UserRolePayload implements Serializable {

    private static final long serialVersionUID = 2069008497215961962L;

    private UserRole userRole;

    public UserRolePayload() {
        userRole = new UserRole();
    }

    public UserRolePayload(UserRole role) {
        userRole = role;
    }

    public String getName() {
        return userRole.getName();
    }

    public void setName(String name) {
        userRole.setName(name);
    }

    public Long getKeyId() {
        return userRole.getKey().getId();
    }

    @JsonIgnore
    public UserRole getUserRole() {
        return userRole;
    }
}
