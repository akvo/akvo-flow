/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.common.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * annotation used to designate that a property of a domain object is "mappable" meaning that it can
 * be configured to populate the field of yet another domain object. A concrete example of this is
 * that AccessPoint has a number of MappableFields which are populated by survey instances based on
 * mapping configuration created by the users. Mappable fields have a display name useful for
 * listing the fields in the UI in a user-friendly manner.
 * 
 * @author Christopher Fagiani
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MappableField {

    String displayName();
}
