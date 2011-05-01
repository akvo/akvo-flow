package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dao.SurveyAttributeMappingDao;
import org.waterforpeople.mapping.domain.SurveyAttributeMapping;

import com.gallatinsystems.common.util.ClassAttributeUtil;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * Service to list/edit/create surveyAttributeMapping objects which are used to
 * map survey questions to attributes of another object
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAttributeMappingServiceImpl extends RemoteServiceServlet
		implements SurveyAttributeMappingService {

	private static final long serialVersionUID = 3087554890568140336L;
	private static final Logger logger = Logger
			.getLogger(SurveyAttributeMappingServiceImpl.class.getName());
	private SurveyAttributeMappingDao mappingDao;

	public SurveyAttributeMappingServiceImpl() {
		mappingDao = new SurveyAttributeMappingDao();
	}

	/**
	 * lists all mappings for a single survey
	 */
	@Override
	public ArrayList<SurveyAttributeMappingDto> listMappingsBySurvey(
			Long surveyId) {
		List<SurveyAttributeMapping> mappingList = mappingDao
				.listMappingsBySurvey(surveyId);
		ArrayList<SurveyAttributeMappingDto> dtoList = null;
		if (mappingList != null) {
			dtoList = new ArrayList<SurveyAttributeMappingDto>();
			for (SurveyAttributeMapping mapping : mappingList) {
				SurveyAttributeMappingDto dto = new SurveyAttributeMappingDto();
				DtoMarshaller.getInstance().copyToDto(mapping, dto);
				dto.setApTypes(null);
				if (mapping.getApTypes() != null) {
					List<String> newList = new ArrayList<String>();
					newList.addAll(mapping.getApTypes());
					dto.setApTypes(newList);
				}
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * lists all mappable attributes for a given object
	 */
	@Override
	public TreeMap<String, String> listObjectAttributes(String objectName) {
		return ClassAttributeUtil.listObjectAttributes(objectName);

	}

	/**
	 * saves all the mappings in a list
	 */
	@Override
	public ArrayList<SurveyAttributeMappingDto> saveMappings(
			ArrayList<SurveyAttributeMappingDto> mappings) {
		if (mappings != null && mappings.size() > 0) {
			// first, delete all the old mappings
			mappingDao.deleteMappingsForSurvey(mappings.get(0).getSurveyId());

			for (SurveyAttributeMappingDto dto : mappings) {
				try {
					SurveyAttributeMapping domain = new SurveyAttributeMapping();
					DtoMarshaller.getInstance().copyToCanonical(domain, dto);
					domain = mappingDao.save(domain);
					dto.setKeyId(domain.getKey().getId());
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Could not save mapping", e);
				}
			}
		}
		return mappings;
	}

	/**
	 * saves all the mappings in a list for a specific question group
	 */
	@Override
	public ArrayList<SurveyAttributeMappingDto> saveMappings(
			Long questionGroupId, ArrayList<SurveyAttributeMappingDto> mappings) {
		if (mappings != null && mappings.size() > 0) {
			// first, delete all the old mappings
			mappingDao.deleteMappingsForQuestionGroup(questionGroupId);

			for (SurveyAttributeMappingDto dto : mappings) {
				try {
					SurveyAttributeMapping domain = new SurveyAttributeMapping();
					DtoMarshaller.getInstance().copyToCanonical(domain, dto);
					domain = mappingDao.save(domain);
					dto.setKeyId(domain.getKey().getId());
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Could not save mapping", e);
				}
			}
		}
		return mappings;
	}

}
