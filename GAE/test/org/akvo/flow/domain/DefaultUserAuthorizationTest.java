/*
 *  Copyright (C) 2022 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.

 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.flow.domain;

import com.gallatinsystems.user.dao.UserRoleDao;
import com.gallatinsystems.user.domain.UserAuthorization;
import com.gallatinsystems.user.domain.UserRole;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultUserAuthorizationTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @BeforeEach
    public void setUp() { helper.setUp(); }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    void testDefaultUserAuthorizationCreation()  {
        UserAuthorization defaultAuth = DefaultUserAuthorization.getOrCreateDefaultAuthorization(12345L, 678910L, "Folder Default");
        UserRole role = new UserRoleDao().findUserRoleByName("DefaultUserRole");

        assertNotNull(role);
        assertEquals(role.getKey().getId(), defaultAuth.getRoleId());
    }
}
