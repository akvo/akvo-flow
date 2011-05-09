package com.gallatinsystems.standards.dao;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.domain.StandardScoring;

public class StandardScoringDao extends BaseDAO<StandardScoring> {

	public StandardScoringDao() {
		super(StandardScoring.class);
		// TODO Auto-generated constructor stub
	}

	public List<StandardScoring> listStandardScoring(AccessPoint ap) {
		List<StandardScoring> ssList = new ArrayList<StandardScoring>();
		ssList = super.listByProperty("pointType", ap.getPointType(), "String");
		return ssList;
	}

	public StandardScoring save(StandardScoring item) {
		return super.saveOrUpdate(item);
	}

}
