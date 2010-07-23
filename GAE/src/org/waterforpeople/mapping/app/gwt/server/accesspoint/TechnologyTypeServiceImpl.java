package org.waterforpeople.mapping.app.gwt.server.accesspoint;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.TechnologyTypeDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.TechnologyTypeService;
import org.waterforpeople.mapping.domain.TechnologyType;
import org.waterforpeople.mapping.helper.TechnologyTypeHelper;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TechnologyTypeServiceImpl extends RemoteServiceServlet implements TechnologyTypeService {
	
	private static final long serialVersionUID = 6949048018976964011L;
	@SuppressWarnings("unused")
	private static final Logger log = Logger
	.getLogger(TechnologyTypeServiceImpl.class.getName());


	public void delete(Long id) {
		TechnologyTypeHelper tth = new TechnologyTypeHelper();
		TechnologyType toObject = new TechnologyType();
		
		toObject = tth.getTechnologyType(id);
		tth.delete(toObject);
		
		
	}

	public TechnologyTypeDto get(Long id) {
		TechnologyTypeHelper tth = new TechnologyTypeHelper();
		return copyCanonicaltoDto(tth.getTechnologyType(id));
	}

	public ArrayList<TechnologyTypeDto> list() {
		TechnologyTypeHelper techTypeHelper = new TechnologyTypeHelper();
		ArrayList<TechnologyTypeDto> list = new ArrayList<TechnologyTypeDto>();
		 for(TechnologyType item :techTypeHelper.listTechnologyTypes()){
			 list.add(copyCanonicaltoDto(item));
		 }
		 return list;
	}

	public TechnologyTypeDto save(TechnologyTypeDto techTypeDto) {
		TechnologyTypeHelper tth = new TechnologyTypeHelper();
		return copyCanonicaltoDto(tth.save(copyDtotoCanonical(techTypeDto)));
	}
	
	private TechnologyTypeDto copyCanonicaltoDto(TechnologyType fromObject){
		TechnologyTypeDto toObject = new TechnologyTypeDto();
		toObject.setCode(fromObject.getCode());
		toObject.setDescription(fromObject.getDescription());
		toObject.setEffectiveEndDate(fromObject.getEffectiveEndDate());
		toObject.setEffectiveStartDate(fromObject.getEffectiveStartDate());
		toObject.setKeyId(fromObject.getKey().getId());
		toObject.setName(fromObject.getName());
		return toObject;
	}
	
	private TechnologyType copyDtotoCanonical(TechnologyTypeDto fromObject){
		TechnologyType toObject = new TechnologyType();
		if (fromObject.getKeyId() != null) {
			Key key = KeyFactory.createKey(TechnologyType.class.getSimpleName(),
					fromObject.getKeyId());
			toObject.setKey(key);
		}
		toObject.setCode(fromObject.getCode());
		toObject.setDescription(fromObject.getDescription());
		toObject.setEffectiveEndDate(fromObject.getEffectiveEndDate());
		toObject.setEffectiveStartDate(fromObject.getEffectiveStartDate());
		toObject.setName(fromObject.getName());
		return toObject;
	}

}
