/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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

package org.akvo.gae.remoteapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

/*
 * Exports GAE datastore data to PostgreSQL
 * Notes:
 * PG version 9.4+, with local connections set to 'trust'
 * The database must be created upfront
 */
public class ExportDataToPG implements Process {

    private static final int BATCH_SIZE = 2000;
    private static final String[] KINDS = {
            Kind.SURVEY_GROUP, Kind.FORM, Kind.QUESTION_GROUP,
            Kind.QUESTION, Kind.DATA_POINT, Kind.FORM_INSTANCE,
            Kind.DEVICE_FILE, Kind.ANSWER
    };
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s (id bigint, created_at bigint, payload jsonb)";
    private static final String INSERT = "INSERT INTO %s VALUES (?, ?, ?)";

    private static final Map<String, String> TABLE_NAME = new HashMap<>();

    static {
        TABLE_NAME.put(Kind.ANSWER, "answer");
        TABLE_NAME.put(Kind.DATA_POINT, "data_point");
        TABLE_NAME.put(Kind.DEVICE_FILE, "device_file");
        TABLE_NAME.put(Kind.FORM, "form");
        TABLE_NAME.put(Kind.FORM_INSTANCE, "form_instance");
        TABLE_NAME.put(Kind.QUESTION, "question");
        TABLE_NAME.put(Kind.QUESTION_GROUP, "question_group");
        TABLE_NAME.put(Kind.SURVEY_GROUP, "survey_group");
    }

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        Class.forName("org.postgresql.Driver");
        String dbName = args[0].replaceAll("-", "_");
        String url = String.format("jdbc:postgresql://localhost/%s?user=postgres", dbName);
        Connection conn = DriverManager.getConnection(url);
        conn.setAutoCommit(false);

        long t0 = System.currentTimeMillis();
        ObjectMapper om = new ObjectMapper();

        for (String kind : KINDS) {

            String tableName = TABLE_NAME.get(kind);

            PreparedStatement ps0 = conn.prepareStatement(String.format(CREATE_TABLE, tableName));
            ps0.execute();
            conn.commit();
            ps0.close();

            Query q = new Query(kind);

            PreparedStatement ps = conn
                    .prepareStatement(String.format(INSERT, tableName));

            long i = 0;
            long t1 = System.currentTimeMillis();

            for (Entity e : ds.prepare(q)
                    .asIterable(FetchOptions.Builder.withChunkSize(BATCH_SIZE))) {

                Date createdDateTime = (Date) e.getProperty("createdDateTime");
                long createdAt = createdDateTime == null ? 0 : createdDateTime.getTime();

                String val = om.writeValueAsString(e).replaceAll("\\\\u0000", "");

                PGobject payload = new PGobject();
                payload.setType("jsonb");
                payload.setValue(val);

                Long entityId = e.getKey().getId();
                Long parentId = e.getKey().getParent() == null ? 0 : e.getKey().getParent().getId();

                ps.setLong(1, entityId + parentId);
                ps.setLong(2, createdAt);
                ps.setObject(3, payload);

                ps.executeUpdate();
                i++;

                if (i % BATCH_SIZE == 0) {
                    System.out.print(".");
                    conn.commit();
                }

            }

            ps.close();
            conn.commit();
            System.out.println("\n");
            System.out.println(kind + " " + (System.currentTimeMillis() - t1) + " ms");
        }
        conn.close();
        System.out.println("Total " + (System.currentTimeMillis() - t0) + " ms");
    }
}
