package org.waterforpeople.mapping.app.gwt.server.formdefinition;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.formdefinition.DataEntryFormDefinitionDto;
import org.waterforpeople.mapping.app.gwt.client.formdefinition.DataEntryFormDefinitionManagerService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.common.dataentry.domain.DataEntryFormDefinition;
import com.gallatinsystems.common.dataentry.helper.DataEntryFormDefinitionHelper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DataEntryFormDefinitionServiceImpl extends RemoteServiceServlet
		implements DataEntryFormDefinitionManagerService, Serializable {

	private DataEntryFormDefinitionHelper helper = null;

	public DataEntryFormDefinitionServiceImpl() {
		helper = new DataEntryFormDefinitionHelper();
	}

	private static final Logger log = Logger
			.getLogger(DataEntryFormDefinitionServiceImpl.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = -9016921150148102482L;

	public void delete(DataEntryFormDefinitionDto item) {
		// TODO Auto-generated method stub

	}

	public DataEntryFormDefinitionDto getByName(String name) {
		DataEntryFormDefinitionDto dest = new DataEntryFormDefinitionDto();
		DataEntryFormDefinition orig = helper.getByName(name);
		DtoMarshaller<DataEntryFormDefinition,DataEntryFormDefinitionDto> marshaller = new DtoMarshaller<DataEntryFormDefinition, DataEntryFormDefinitionDto>(orig, dest);
		marshaller.copyToDto(dest, orig);
		return dest;
	}

	public ArrayList<DataEntryFormDefinitionDto> list() {
		// TODO Auto-generated method stub
		return null;
	}

	public DataEntryFormDefinitionDto save(DataEntryFormDefinitionDto orig) {
		DataEntryFormDefinition dest = new DataEntryFormDefinition();
		DtoMarshaller<DataEntryFormDefinition,DataEntryFormDefinitionDto> marshaller = new DtoMarshaller<DataEntryFormDefinition, DataEntryFormDefinitionDto>(dest, orig);
		
		marshaller.copyToCanonical(dest, orig, DataEntryFormDefinition.class);
		marshaller.copyToDto(orig, helper.save(dest));
		return orig;
	}

	public DataEntryFormDefinitionDto copyToDto(DataEntryFormDefinition from) {
		DataEntryFormDefinitionDto to = new DataEntryFormDefinitionDto();
		try {
			BeanUtils.copyProperties(to, from);
			to.setKeyId(from.getKey().getId());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return to;
	}
}
