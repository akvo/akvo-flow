package com.gallatinsystems.common.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(value=RetentionPolicy.RUNTIME)

public @interface DisplayName {

	String value();
}
