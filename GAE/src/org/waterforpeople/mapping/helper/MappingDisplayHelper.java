package org.waterforpeople.mapping.helper;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.app.VelocityEngine;

public class MappingDisplayHelper {

	private static final Logger log = Logger
			.getLogger(MappingDisplayHelper.class.getName());

	public enum MapType {
		GOOGLE, GRASS
	};

	public String generateMap(String criteria, MapType type) {
		if (type.equals(MapType.GOOGLE))
			assembleKML(criteria);
		return null;
	}

	private String assembleKML(String criteria) {
		return null;
	}

	private String buildPlacemarks() {
		return null;
	}

	private String buildRegionOutlines() {
		return null;
	}

	private String buildDocument() {
		StringBuilder kmlSB = new StringBuilder();
		kmlSB.append(buildPlacemarks());
		kmlSB.append(buildRegionOutlines());
		return kmlSB.toString();
	}

	private VelocityEngine engine = null;

	private void setupEngine() {
		engine = new VelocityEngine();
		engine.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.NullLogChute");
		try {
			engine.init();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not initialize velocity", e);
		}
	}
}
