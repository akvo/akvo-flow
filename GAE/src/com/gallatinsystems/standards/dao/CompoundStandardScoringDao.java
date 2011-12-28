package com.gallatinsystems.standards.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.domain.CompoundStandardScoringRule;
import com.google.appengine.api.datastore.Key;

public class CompoundStandardScoringDao extends BaseDAO<CompoundStandardScoringRule> {

	public CompoundStandardScoringDao( ) {
		super(CompoundStandardScoringRule.class);
	}
	
	public CompoundStandardScoringRule save(CompoundStandardScoringRule item){
		StandardScoringDao ssdao = new StandardScoringDao();
		if(item.getStandardScoreLeft()!=null){
			 item.setStandardScoreIdLeft(ssdao.save(item.getStandardScoreLeft()).getKey().getId());
		}
		if(item.getStandardScoreRight()!=null){
			item.setStandardScoreIdRight(ssdao.save(item.getStandardScoreRight()).getKey().getId());
		}
		return super.save(item);
	}
	
	@Override 
	public CompoundStandardScoringRule getByKey(Long id){
		CompoundStandardScoringRule item = super.getByKey(id);
		StandardScoringDao ssDao = new StandardScoringDao();
		item.setStandardScoreLeft(ssDao.getByKey(item.getStandardScoreIdLeft()));
		item.setStandardScoreRight(ssDao.getByKey(item.getStandardScoreIdRight()));
		return item;
	}
	
	@Override
	public CompoundStandardScoringRule getByKey(Key key){
		return this.getByKey(key.getId());
	}
}
