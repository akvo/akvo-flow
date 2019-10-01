package com.gallatinsystems.framework.dao;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class AllowedResponse implements Serializable {
    Set<Long> securedObjectIds;
    boolean isSuperAdmin;

    public AllowedResponse() {

    }

    public AllowedResponse(boolean superAdmin) {
        this.isSuperAdmin = superAdmin;
        this.securedObjectIds = null;
    }

    public AllowedResponse(Set<Long> securedObjectIds) {
        this.isSuperAdmin = false;
        this.securedObjectIds = securedObjectIds;
    }

    public void setSecuredObjectIds(Set<Long> securedObjectIds) {
        this.securedObjectIds = securedObjectIds;
    }

    public void setIsSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    public Set<Long> getSecuredObjectIds() {
        return securedObjectIds;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllowedResponse that = (AllowedResponse) o;
        return isSuperAdmin == that.isSuperAdmin &&
                Objects.equals(securedObjectIds, that.securedObjectIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(securedObjectIds, isSuperAdmin);
    }
}
