package org.waterforpeople.mapping.dao;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;

import services.S3Driver;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This data access object will persist and retrieve Geo information from the
 * GIS index. This implementation uses JTS (see
 * http://www.vividsolutions.com/jts/jtshome.htm).
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoIndexDao {

	/**
	 * this will create (or replace) a geo index for each of the regions passed
	 * in.
	 * 
	 * @param regions
	 *            - map with regions uuid as key and POLYGON WellKnownFormat
	 *            strings as values
	 */
	public void saveRegionIndex(Map<String, String> regions) {
		GeometryFactory factory;
		STRtree index = new STRtree();
		try {
			S3Driver s3Driver = new S3Driver();
			for (String region : regions.keySet()) {
				factory = new GeometryFactory();
				WKTReader reader = new WKTReader(factory);
				Polygon regionPolygon = (Polygon) reader.read(regions
						.get(region));

				index
						.insert(regionPolygon.getEnvelopeInternal(),
								regionPolygon);

				index.build();

				ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(byteArr);
				oos.writeObject(index);

				// TODO: change path
				// NOTE: this will give an exception in the Dev environment due
				// to a bug with the GAE local implementation. It'll work on the
				// server.
				s3Driver.uploadFile("dru-test", "gis/index/" + region, byteArr
						.toByteArray());

				oos.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public STRtree findGeoIndex(String regionUUID){
		STRtree index = null;
		//ObjectInputStream()
		
		
		return index;
	}

}
