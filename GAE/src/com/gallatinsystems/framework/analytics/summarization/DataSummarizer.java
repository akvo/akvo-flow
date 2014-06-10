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

package com.gallatinsystems.framework.analytics.summarization;

/**
 * Classes that implement this interface will perform some sort of data summarization and (most
 * likely) persist the result.
 * 
 * @author Christopher Fagiani
 */
public interface DataSummarizer {

    public static final int BATCH_SIZE = 10;

    /**
     * perform and store the summarization.
     * 
     * @param key
     * @param type
     * @param value - payload
     * @param offset - integer offset used for batching processing. This offset is used for batching
     *            at the input object level.
     * @param cursor - used for batching based on results from the data store
     * @return - true if summarization completed, false if not
     */
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor);

    /**
     * returns the current cursor string, if any
     * 
     * @return
     */
    public String getCursor();
}
