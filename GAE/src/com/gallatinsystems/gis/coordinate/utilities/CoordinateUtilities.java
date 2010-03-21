package com.gallatinsystems.gis.coordinate.utilities;

public class CoordinateUtilities {
	public String convertDecimalToDegrees(Double lat, Double lon){
		String degrees = null;
		Long latDecimal = 0L;
		
		Long lonDecimal = 0L;
		
		latDecimal =lat.longValue();
		Double degreesLat = (lat-latDecimal)*60;
		
		lonDecimal = lon.longValue();
		Double degreesLon = (lon-lonDecimal)*60;
		
		degrees = "lat: "+latDecimal + " degrees " + degreesLat;
		degrees += "lon: " + lonDecimal + " degrees " + degreesLon;
		
		return degrees;
	}

	
	public static void main(String[] args){
		CoordinateUtilities cu = new CoordinateUtilities();
		System.out.println(cu.convertDecimalToDegrees(new Double(args[0]),new Double(args[1])));
	}
}
