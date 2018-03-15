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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

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
 * - Fixes bad filenames in DeviceFileJobQueue entities
 */
public class FixDeviceFileJobQueue implements Process {

    private int allDevicesJobs = 0, jsonDeviceJobs = 0;
    private Map<Long, String> devices = new HashMap<>();
    private List<Entity>fixupList = new ArrayList();
    private boolean doBatch = true; // any failure will mean nothing is updated
    
    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        processDeviceFileJobQueue(ds);

        System.out.printf("#DeviceFileJobs:  %d/%d with bad filenames\n", jsonDeviceJobs, allDevicesJobs);
        

    }

        
    private void processDeviceFileJobQueue(DatastoreService ds) throws InterruptedException {

        System.out.println("#Processing DeviceFileJobQueue");
        
        //find all jobs made since we changed from wfpPhotonnnn format
        final Filter f = new FilterPredicate("fileName", FilterOperator.LESS_THAN, "wfp");
        final Filter f2 = new FilterPredicate("fileName", FilterOperator.GREATER_THAN, "ff");
        final Query group_q = new Query("DeviceFileJobQueue");//.setFilter(f).setFilter(f2);
        final PreparedQuery pq = ds.prepare(group_q);

        for (Entity job : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            allDevicesJobs++;
            Long dfjId = job.getKey().getId();
            String name = (String) job.getProperty("fileName");
            if (name.endsWith("}")) { //JSON - must fix up
                jsonDeviceJobs++;
                String name2 = name.substring(0,name.indexOf("\""));//strip rest of JSON "container"
                System.out.println(name + " --> " + name2);
                job.setProperty("fileName", name2);
                fixupList.add(job);
                if (!doBatch) {
                    ds.put(job);
                    Thread.sleep(100);//short delay to lessen server load
                } else if (fixupList.size()>99){
                    System.out.printf("#Fixing %d Jobs\n",fixupList.size());
                    batchSaveEntities(ds, fixupList);
                    fixupList.clear();
                }

            }
//            System.out.printf("#INF All-device job %d '%s'\n", dfjId, name);
        }
    }

}
