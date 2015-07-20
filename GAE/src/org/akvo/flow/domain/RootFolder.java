
package org.akvo.flow.domain;

import java.util.Collections;
import java.util.List;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * An object to represent the root folder
 */
public class RootFolder implements SecuredObject {

    @Override
    public SecuredObject getParentObject() {
        return null;
    }

    @Override
    public List<BaseDomain> updateAncestorIds(boolean cascade) {
        return Collections.emptyList();
    }

    @Override
    public Long getObjectId() {
        return 0L;
    }

    @Override
    public List<Long> listAncestorIds() {
        return Collections.emptyList();
    }

}
