package org.waterforpeople.mapping.dao;

import java.util.List;

import org.waterforpeople.mapping.domain.AccessPointScoreDetail;

import com.gallatinsystems.framework.dao.BaseDAO;

public class AccessPointScoreDetailDao extends BaseDAO<AccessPointScoreDetail> {

	public AccessPointScoreDetailDao() {
		super(AccessPointScoreDetail.class);
		// TODO Auto-generated constructor stub
	}

	public List<AccessPointScoreDetail> listByAccessPointId(Long accessPointId) {
		return super.listByProperty("accessPointId", accessPointId, "Long",
				"computationDate", "desc");
	}
}
