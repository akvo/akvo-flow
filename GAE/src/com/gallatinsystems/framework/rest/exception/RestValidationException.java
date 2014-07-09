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

package com.gallatinsystems.framework.rest.exception;

import java.util.List;

import com.gallatinsystems.framework.rest.RestError;

/**
 * Validation errors for rest input parameters
 * 
 * @author Christopher Fagiani
 */
public class RestValidationException extends RestException {

    private static final long serialVersionUID = 9185247960286459263L;

    public RestValidationException(List<RestError> errors, String message,
            Exception rootCause) {
        super(errors, message, rootCause);
    }

    public RestValidationException(RestError error, String message,
            Exception rootCause) {
        super(error, message, rootCause);
    }
}
