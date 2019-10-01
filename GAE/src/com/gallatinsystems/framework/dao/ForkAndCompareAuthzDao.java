package com.gallatinsystems.framework.dao;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.User;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;

public class ForkAndCompareAuthzDao implements AuthzDao {
    private AuthzDaoGaeStorage authzDaoGaeStorage = new AuthzDaoGaeStorage();
    private HttpAuthzDao httpAuthzDao = new HttpAuthzDao();

    private static final Logger logger = Logger.getLogger(ForkAndCompareAuthzDao.class.getName());
    private static final Logging SERVICE = LoggingOptions.getDefaultInstance().getService();

    @Override
    public AllowedResponse getAllowedObjects(final Long flowUserId) {
        long start = System.currentTimeMillis();

        final CompletableFuture<Pair<Long, AllowedResponse>> completableFuture
                = new CompletableFuture<>();

        Runnable runnable = new Runnable() {
            public void run() {
                long start = System.currentTimeMillis();
                AllowedResponse httpResponse = null;
                try {
                    httpResponse = httpAuthzDao.getAllowedObjects(flowUserId);
                } finally {
                    long responseTime = System.currentTimeMillis() - start;
                    try {
                        Pair<Long, AllowedResponse> gae = completableFuture.get();
                        boolean same = Objects.equals(gae.getRight(), httpResponse);
                        // Store in a new Kind?????
                        log("HTTP=" + responseTime + "," + "GAE=" + gae.getLeft() +",diff=" + (responseTime - gae.getLeft()) + ",same=" + same);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(runnable).start();

        AllowedResponse gaeResponse = null;
        try {
            gaeResponse = authzDaoGaeStorage.getAllowedObjects(flowUserId);
        } finally {
            long responseTime = System.currentTimeMillis() - start;
            completableFuture.complete(Pair.of(responseTime, gaeResponse));
        }
        return gaeResponse;
    }

    // Using the log api because we are logging in a background thread
    private void log(String msg) {
        String logName = "forkandcompare";
        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(msg))
                .setSeverity(Severity.INFO)
                .setLogName(logName)
                .setResource(MonitoredResource.newBuilder("global").build())
                .build();

        // Writes the log entry asynchronously
        SERVICE.write(Collections.singleton(entry));
    }

    @Override
    public boolean hasPermInTree(List<Long> objectIds, Long userId, Permission permission) {
        return authzDaoGaeStorage.hasPermInTree(objectIds, userId, permission);
    }

    @Override
    public String getPermissionsMap(User currentUser) {
        return authzDaoGaeStorage.getPermissionsMap(currentUser);
    }

    @Override
    public User findUserByEmail(String email) {
        return authzDaoGaeStorage.findUserByEmail(email);
    }
}
