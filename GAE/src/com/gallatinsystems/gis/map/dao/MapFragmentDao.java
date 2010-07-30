package com.gallatinsystems.gis.map.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.map.domain.MapFragment;

public class MapFragmentDao extends BaseDAO<MapFragment> {

	public MapFragmentDao() {
		super(MapFragment.class);
	}
	
	public List<MapFragment> getAllCountriesMapFragments(){
		return null;
	}
	
	public MapFragment getFinalCountryMapFragment(String countryCode){
		return null;
	}
	
	public List<MapFragment> findMapFragmentsByCountry(String countryCode){
		return null;
	}

}
