package com.gallatinsystems.standards.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.domain.CompoundStandard;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.google.appengine.api.datastore.Key;

public class CompoundStandardDao extends BaseDAO<CompoundStandard> {

	public CompoundStandardDao( ) {
		super(CompoundStandard.class);
	}
	
	public CompoundStandard save(CompoundStandard item){
		StandardDao ssdao = new StandardDao();
		if(item.getStandardLeft()!=null){
			 item.setStandardIdLeft(ssdao.save(item.getStandardLeft()).getKey().getId());
		}
		if(item.getStandardRight()!=null){
			item.setStandardIdRight(ssdao.save(item.getStandardRight()).getKey().getId());
		}
		return super.save(item);
	}
	
	public List<CompoundStandard> listByType(StandardType type){
		List<CompoundStandard> csList = super.listByProperty("standardType", type, "String");
		StandardDao ssDao = new StandardDao();
		for(CompoundStandard item: csList ){
			item.setStandardLeft(ssDao.getByKey(item.getStandardIdLeft()));
			item.setStandardRight(ssDao.getByKey(item.getStandardIdRight()));
		}
		return csList;
	}
	
	@Override 
	public CompoundStandard getByKey(Long id){
		CompoundStandard item = super.getByKey(id);
		StandardDao ssDao = new StandardDao();
		item.setStandardLeft(ssDao.getByKey(item.getStandardIdLeft()));
		item.setStandardRight(ssDao.getByKey(item.getStandardIdRight()));
		return item;
	}
	
	@Override
	public CompoundStandard getByKey(Key key){
		return this.getByKey(key.getId());
	}
}
