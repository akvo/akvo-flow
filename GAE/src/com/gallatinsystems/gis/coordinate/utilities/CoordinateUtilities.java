package com.gallatinsystems.gis.coordinate.utilities;

public class CoordinateUtilities {
	public String convertDecimalToDegrees(Double lat, Double lon) {
		String degrees = null;
		Long latDecimal = 0L;

		Long lonDecimal = 0L;

		latDecimal = lat.longValue();
		Double degreesLat = (lat - latDecimal) * 60;

		lonDecimal = lon.longValue();
		Double degreesLon = (lon - lonDecimal) * 60;

		degrees = "lat: " + latDecimal + " degrees " + degreesLat;
		degrees += "lon: " + lonDecimal + " degrees " + degreesLon;

		return degrees;
	}

	public static void main(String[] args) {
		CoordinateUtilities cu = new CoordinateUtilities();
		Integer[][] coord = { { 757751, 9170463 }, { 745161, 9180125 },
				{ 743720, 9178676 }, { 739237, 9162141 }, { 736746, 9171754 },
				{ 738684, 9175113 }, { 750890, 9177962 }, { 753027, 9178879 },
				{ 757067, 9232085 }, { 745474, 9222255 }, { 762357, 9236163 },
				{ 737981, 9173005 }, { 751220, 9219068 }, { 736744, 9171747 },
				{ 759539, 9173748 }, { 751381, 9164942 }, { 740637, 9169838 },
				{ 737014, 9160806 }, { 738660, 9172710 }, { 740287, 9173168 },
				{ 749475, 9221649 }, { 753464, 9220705 }, { 743043, 9176716 },
				{ 748213, 9222428 }, { 735706, 9160401 }, { 730908, 9159975 },
				{ 732831, 9159993 }, { 732427, 9160324 }, { 743268, 9167379 },
				{ 754048, 9161662 }, { 756671, 9164999 }, { 755802, 9162895 },
				{ 740910, 9168287 }, { 757778, 9233144 }, { 754735, 9230676 },
				{ 747582, 9178263 }, { 741974, 9174864 }, { 742516, 9181039 },
				{ 756537, 9229557 }, { 748328, 9225052 }, { 756405, 9227727 },
				{ 745044, 9177367 }, { 754165, 9228243 }, { 757921, 9162965 },
				{ 739962, 9169708 }, { 741333, 9162304 }, { 752351, 9221486 },
				{ 739947, 9172180 }, { 757996, 9163834 }, { 754514, 9225569 },
				{ 741623, 9165318 }, { 739753, 9171625 }, { 751334, 9223826 },
				{ 744912, 9169130 }, { 741959, 9163551 }, { 741771, 9167453 },
				{ 741610, 174143 }, { 743262, 9168760 } };
		for (int i = 0; i < coord.length; i++)
			System.out.println(cu.convertUTMtoLatLon(coord[i][0], coord[i][1],
					NSLatitude.SOUTH, 17));
	}

	static final Double polarAxis = 6356752.314;
	static final Integer equRad = 6378137;
	static final Double ecc = 0.081819191;
	static final Double e2 = 0.006739497;
	static final Double k0 = 0.9996;
	static final Double pi = 3.14159265358979323846264338327950288;

	public String convertUTMtoLatLon(Integer eastingCoor, Integer northingCoor,
			NSLatitude lat, Integer zone) {
		Integer zoneCentralLongitude = computeZoneCentralLongitude(zone);

		Double arcLength = (10000000 - northingCoor) / k0;
		// =arc/(a*(1-ec^2/4-3*ec^4/64-5*ec^6/256))

		Double a1 = Math.pow(ecc, 2) / 4;
		Double a2 = 3 * (Math.pow(ecc, 4) / 64);
		Double a3 = 5 * (Math.pow(ecc, 6) / 256);

		Double a = 1 - a1 - a2 - a3;

		Double mu = arcLength / (equRad * a);
		// =(1-(1-ec*ec)^(1/2))/(1+(1-ec*ec)^(1/2))
		Double a4 = Math.sqrt((1 - Math.pow(ecc, 2)));

		Double e1 = (1 - a4) / (1 + a4);
		// =3*ei/2-27*ei^3/32
		Double c1 = ((3 * e1) / 2) - (27 * Math.pow(e1, 3) / 32);
		// =21*ei^2/16-55*ei^4/32
		Double c2 = (21 * Math.pow(e1, 2) / 16) - (55 * Math.pow(e1, 4) / 32);
		Double c3 = (151 * Math.pow(e1, 3) / 96);
		Double c4 = (1097 * Math.pow(e1, 4) / 512);
		// =mu+ca*SIN(2*mu)+cb*SIN(4*mu)+ccc*SIN(6*mu)+cd*SIN(8*mu)
		Double footprintLat = mu + (c1 * Math.sin(2 * mu))
				+ (c2 * Math.sin(4 * mu)) + (c3 * Math.sin(6 * mu))
				+ (c4 * Math.sin(8 * mu));

		// Lat =180*(_phi1-fact1*(fact2+fact3+fact4))/PI()
		// Lon =F3-E22

		Double C1 = e2 * Math.pow(Math.cos(footprintLat), 2);
		Double T1 = Math.pow(Math.tan(footprintLat), 2);
		// =a/(1-(ec*SIN(_phi1))^2)^(1/2)
		Double N1 = equRad
				/ Math.sqrt((1 - (Math.pow(ecc * Math.sin(footprintLat), 2))));
		// =a*(1-ec*ec)/(1-(ec*SIN(_phi1))^2)^(3/2)
		Double R1 = (equRad * (1 - Math.pow(ecc, 2)))
				/ Math.pow((1 - Math.pow(ecc * Math.sin(footprintLat), 2)),
						3 / 2);
		// =H2/(n0*k0)
		Double D = (500000 - eastingCoor) / (N1 * k0);

		// =n0*TAN(_phi1)/r0
		Double fact1 = (N1 * Math.tan(footprintLat)) / R1;
		// =dd0*dd0/2
		Double fact2 = Math.pow(D, 2) / 2;
		// =(5+3*t0+10*Q0-4*Q0*Q0-9*eisq)*dd0^4/24
		Double fact3 = (5 + 3 * T1 + 10 * C1 - 4 * Math.pow(C1, 2) - 9 * e2)
				* Math.pow(D, 4) / 24;
		// =(61+90*t0+298*Q0+45*t0*t0-252*eisq-3*Q0*Q0)*dd0^6/720
		Double fact4 = (61 + 90 * T1 + 298 * e2 + 45 * Math.pow(T1, 2) - 252
				* e2 - 3 * Math.pow(C1, 2))
				* Math.pow(D, 6) / 720;

		// Double latitude = 180*(footprintLat - fact1*(fact2+fact3+fact4))/pi;
		Double latitude;

		latitude = (180 * (footprintLat - fact1 * (fact2 + fact3 + fact4)))
				/ pi;
		latitude = latitude * -1;

		// long = long0 + (Q5 - Q6 + Q7)/cos(fp), where:
		//
		// Q5 = D
		// Q6 = (1 + 2T1 + C1)D3/6
		// Q7 = (5 - 2C1 + 28T1 - 3C12 + 8e'2 + 24T12)D5/120

		Double Q5 = D;
		Double Q6 = (1 + 2 * T1 + C1) * Math.pow(D, 3) / 6;
		Double Q7 = (5 - 2 * C1 + 28 * T1 - 3 * Math.pow(C1, 2) + 8 * e2 + 24 * Math
				.pow(T1, 2))
				* Math.pow(D, 5) / 120;
		// =(_lof1-_lof2+_lof3)/COS(_phi1)
		Double H20 = Q5 - Q6 + Q7 / Math.cos(footprintLat);
		Double E22 = H20 * 180 / pi;
		Double longitude = zoneCentralLongitude - E22;

		return latitude + " " + longitude;
	}

	private Integer computeZoneCentralLongitude(Integer zone) {
		Integer zcl = 0;
		// =IF(E19>0,6*E19-183,3)
		if (zone > 0) {
			zcl = 6 * zone - 183;
		}
		return zcl;
	}

	public static enum NSLatitude {
		NORTH, SOUTH
	};

}
