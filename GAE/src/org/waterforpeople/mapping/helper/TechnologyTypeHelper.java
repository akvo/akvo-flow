package org.waterforpeople.mapping.helper;

import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.TechnologyType;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;

public class TechnologyTypeHelper {
	private static final Logger log = Logger.getLogger(TechnologyTypeHelper.class
			.getName());

	public List<TechnologyType> listTechnologyTypes(){
		BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
		return baseDAO.list(Constants.ALL_RESULTS);
	}
	
	public TechnologyType save(TechnologyType techType){
		BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
		return baseDAO.save(techType);
	}
	
	public void delete(TechnologyType techType){
		BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
		baseDAO.delete(techType);
	}
	
	public TechnologyType getTechnologyType(Long id){
		BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
		return baseDAO.getByKey(id);
	}
	
	public void deleteAll(){
		BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
		for(TechnologyType techType: baseDAO.list(Constants.ALL_RESULTS)){
			baseDAO.delete(techType);
		}
	}
}
