package com.gallatinsystems.common.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * annotation used to designate that a property of a domain object is "mappable"
 * meaning that it can be configured to populate the field of yet another domain
 * object. A concrete example of this is that AccessPoint has a number of
 * MappableFields which are populated by survey instances based on mapping
 * configuration created by the users.
 * 
 * Mappable fields have a display name useful for listing the fields in the UI in a user-friendly manner.
 * 
 * @author Christopher Fagiani
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MappableField {

	String displayName();
}
