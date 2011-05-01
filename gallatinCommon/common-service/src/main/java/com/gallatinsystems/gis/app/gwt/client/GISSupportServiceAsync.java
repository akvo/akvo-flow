package com.gallatinsystems.gis.app.gwt.client;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GISSupportServiceAsync {

	void listCountryCodes(AsyncCallback<TreeMap<String, String>> callback);

	void listCoordinateTypes(AsyncCallback<TreeMap<String, String>> callback);

	void listFeatureTypes(AsyncCallback<TreeMap<String, String>> callback);

	void listUTMZones(AsyncCallback<ArrayList<Integer>> callback);

}
