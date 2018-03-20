/*
 *  Copyright (C) 2018 Stichting Akvo (Akvo Foundation)
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

import static org.akvo.gae.remoteapi.DataUtils.batchSaveEntities;
import static org.akvo.gae.remoteapi.DataUtils.batchDelete;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/*
 * - Checks that all surveys, groups, questions and options are consistent
 */
public class CheckTrimDevicesAndDeviceFiles implements Process {

    private int orphanSurveys = 0, jobs = 0, allDevicesJobs = 0, oldJobs = 0, badJobs = 0, orphanJobs = 0;
    private int s3errors = 0, s3timeouts = 0;
    private Map<Long, String> devices = new HashMap<>();
    private Map<Long, String> deviceFiles = new HashMap<>();
    private List<Key>oldEntities = new ArrayList<>();

    private boolean retireOld = false; // Make question survey pointer match the group's
    
    Date now = new Date();
    Date then = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String baseUrl;
    
    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.printf("#Arguments: [date [baseurl][--retire]] to show/remove deviceFiles older than date.\n");
//        for (int i = 0; i < args.length; i++) {
//            System.out.printf("#Argument %d: %s\n", i, args[i]);
//        }
        if (args.length > 0) {
            then = df.parse(args[0]);
        }
        if (args.length > 1) {
            baseUrl = args[1];
        }
        if (args.length > 1  && args[1].equalsIgnoreCase("--retire")
                || args.length > 2  && args[2].equalsIgnoreCase("--retire")) {
            retireOld = true;            
        }

        processDevices(ds);
        processDeviceFiles(ds);

        System.out.printf("#Devices:         %5d total, %d older than %tF\n", devices.size(), orphanSurveys, then);
        System.out.printf("#DeviceFileJobs:  %5d total, %d all-device, %d old, %d bad, %d orphan\n", jobs, allDevicesJobs, oldJobs, badJobs, orphanJobs);
        System.out.printf("#S3: %d timeouts, %d errors\n", s3timeouts, s3errors);
        

        if (retireOld) {
            System.out.printf("#INF Deleting %d entites\n", oldEntities.size());
            batchDelete(ds, oldEntities);
        }

    }

    

    private void processDevices(DatastoreService ds) throws ParseException {

        System.out.println("#Processing Devices");

        final Query group_q = new Query("Device");
        final PreparedQuery group_pq = ds.prepare(group_q);

        for (Entity g : group_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long deviceId = g.getKey().getId();
            String name = (String) g.getProperty("deviceIdentifier");
            Date lastBeacon = (Date) g.getProperty("lastLocationBeaconTime"); //TODO: if null
            if (lastBeacon == null) lastBeacon = df.parse("2000-01-01");
            Date lastModified = (Date) g.getProperty("lastUpdateDateTime");
            //Long beaconDays = now-lastBeacon
            System.out.printf("#INF %s Device beacon %tF, mod %tF '%s'\n",
                    then.after(lastBeacon)?"OLD":"NEW",
                    lastBeacon,
                    lastModified,
                    name
                    );
            if (then.after(lastBeacon)) {
                orphanSurveys++;
//                oldEntities.add(g.getKey());
            }
            devices.put(deviceId, name);
        }
    }

    
    private void processDeviceFiles(DatastoreService ds) throws MalformedURLException {

        System.out.println("#Processing DeviceFileJobQueue");
        
        //find jobs for "unknown" device (= for all devices)
        final Filter f = new FilterPredicate("deviceId", FilterOperator.EQUAL, null);
        final Query group_q = new Query("DeviceFileJobQueue");//.setFilter(f);
        final PreparedQuery pq = ds.prepare(group_q);

        for (Entity g : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long dfjId = g.getKey().getId();
            Long devId = (Long) g.getProperty("deviceId");
            String name = (String) g.getProperty("fileName");
            Date lastModified = (Date) g.getProperty("lastUpdateDateTime");
            jobs++;
            if (devId == null) {
                allDevicesJobs++;
            }
            
            String state = then.after(lastModified)?"OLD":"NEW";
            if (baseUrl != null && presentInS3(name)) {
                state = "BAD";
                badJobs++;
                oldEntities.add(g.getKey());
            } else if (devId != null && !devices.containsKey(devId)) {
                state = "ORPHAN";
                orphanJobs++;
                oldEntities.add(g.getKey());
            }
            
            if (lastModified == null || then.after(lastModified)) {
                oldJobs++;
                oldEntities.add(g.getKey());
            }
            System.out.printf("#INF %s job (device %d) #%d filename '%s'\n",
                    state,
                    devId,
                    dfjId,
                    name
                    );

            //progressive delete so an error does not fail to delete anything
            if (retireOld && oldEntities.size() >= 1000) {
                System.out.printf("#DEL Deleting %d entites\n", oldEntities.size());
                batchDelete(ds, oldEntities);
                oldEntities.clear();
            }
                    
        }
    }
    
    private boolean presentInS3(String fn) throws MalformedURLException {
        final String imageUrl = baseUrl + "/" + fn;

        // MalformedURLException exception caught by method signature
        final URL url = new URL(imageUrl);

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60000); //one minute
            conn.setRequestMethod("HEAD");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                System.out.printf("Got %d while checking %s\n", conn.getResponseCode(), imageUrl);
            }
        } catch (SocketTimeoutException timeout) {
            // reschedule the task without delay
            // Possible a hiccup in GAE side
            s3timeouts++;
        } catch (IOException e) {
            // IOException possible a http 403, reschedule the task
            s3errors++;
        }
        return false;
        
    }
}
