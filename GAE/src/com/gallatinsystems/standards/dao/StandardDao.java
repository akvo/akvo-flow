package com.gallatinsystems.standards.dao;

import java.util.List;

import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.domain.Standard;

public class StandardDao extends BaseDAO<Standard> {
	public StandardDao(){
		super(Standard.class);
	}
	
	
	public List<Standard> listByAccessPointType(AccessPointType accessPointType){
		return super.listByProperty("accessPointType", accessPointType, "String");
	}
}
