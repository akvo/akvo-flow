package org.waterforpeople.mapping.app.web.test;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

public class DeleteObjectUtil {
	public void deleteAllObjects(String kind) {
		final DatastoreService dss = DatastoreServiceFactory
		.getDatastoreService();

		final Query query = new Query(kind);

		query.setKeysOnly();

		final ArrayList<Key> keys = new ArrayList<Key>();

		for (final Entity entity : dss.prepare(query).asIterable(
				FetchOptions.Builder.withLimit(100000))) {
			keys.add(entity.getKey());
		}
		dss.delete(keys);
	}
}
