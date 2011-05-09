package com.gallatinsystems.standards.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.domain.StandardScoring;

public class StandardScoringDao extends BaseDAO<StandardScoring> {

	public StandardScoringDao() {
		super(StandardScoring.class);
		// TODO Auto-generated constructor stub
	}

	public List<StandardScoring> listStandardScoring(String pointType) {
		return super.listByProperty("pointType", pointType, "String");
	}

	public StandardScoring save(StandardScoring item) {
		return super.saveOrUpdate(item);
	}

}
