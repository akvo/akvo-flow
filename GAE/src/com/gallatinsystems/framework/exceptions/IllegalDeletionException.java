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

package com.gallatinsystems.framework.exceptions;

/**
 * Exception to be thrown if something attempts to perform an illegal deletion. An example of an
 * illegal deletion is if a user tries to delete a survey Question that already has responses in the
 * system.
 * 
 * @author Christopher Fagiani
 */
public class IllegalDeletionException extends Exception {

    private static final long serialVersionUID = -8158600040817892556L;
    private String error;

    public IllegalDeletionException() {
        this("unknown", null);
    }

    public IllegalDeletionException(String err) {
        this(err, null);
    }

    public IllegalDeletionException(String msg, Exception root) {
        super(msg, root);
        error = msg;
    }

    public String getError() {
        return error;
    }
}
