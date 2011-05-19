package org.waterforpeople.mapping.app.gwt.server.accesspoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.TechnologyTypeDto;
import org.waterforpeople.mapping.app.util.AccessPointServiceSupport;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import services.S3Driver;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.gis.geography.dao.CountryDao;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.image.GAEImageAdapter;
import com.gallatinsystems.image.ImageUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AccessPointManagerServiceImpl extends RemoteServiceServlet
		implements AccessPointManagerService {
	
	private static final String S3_BUCKET = "s3bucket";

	private static final Logger log = Logger
			.getLogger(AccessPointManagerService.class.getName());

	private AccessPointHelper aph;
	private static final long serialVersionUID = 2710084399371519003L;

	public AccessPointManagerServiceImpl() {
		aph = new AccessPointHelper();
	}

	/**
	 * lists all access points that match the search criteria passed in
	 */
	@Override
	public ResponseDto<ArrayList<AccessPointDto>> listAccessPoints(
			AccessPointSearchCriteriaDto searchCriteria, String cursorString) {
		AccessPointDao dao = new AccessPointDao();

		List<AccessPoint> pointList = dao.searchAccessPoints(searchCriteria
				.getCountryCode(), searchCriteria.getCommunityCode(),
				searchCriteria.getCollectionDateFrom(), searchCriteria
						.getCollectionDateTo(), searchCriteria.getPointType(),
				searchCriteria.getTechType(), searchCriteria
						.getConstructionDateFrom(), searchCriteria
						.getConstructionDateTo(), searchCriteria.getOrderBy(),
				searchCriteria.getOrderByDir(), searchCriteria.getPageSize(),cursorString);
		ArrayList<AccessPointDto> apDtoList = new ArrayList<AccessPointDto>();
		for (AccessPoint apItem : pointList) {
			AccessPointDto apDto = AccessPointServiceSupport
					.copyCanonicalToDto(apItem);
			apDtoList.add(apDto);
		}

		ResponseDto<ArrayList<AccessPointDto>> container = new ResponseDto<ArrayList<AccessPointDto>>();
		container.setCursorString(AccessPointDao.getCursor(pointList));
		container.setPayload(apDtoList);

		return container;
	}

	@Override
	public List<AccessPointDto> listAllAccessPoints(String cursorString) {
		List<AccessPointDto> apDtoList = new ArrayList<AccessPointDto>();

		for (AccessPoint apItem : aph.listAccessPoint(cursorString)) {
			AccessPointDto apDto = AccessPointServiceSupport
					.copyCanonicalToDto(apItem);
			apDtoList.add(apDto);
		}

		return apDtoList;
	}

	@Override
	public Integer deleteAccessPoint(Long id) {
		AccessPointDao apDao = new AccessPointDao();
		apDao.delete(apDao.getByKey(id));
		return 1;
	}
	
	@Override
	public void deleteAccessPoints(AccessPointSearchCriteriaDto searchCriteria){
		AccessPointDao apDao = new AccessPointDao();
		apDao.deleteByQuery(searchCriteria
				.getCountryCode(), searchCriteria.getCommunityCode(),
				searchCriteria.getCollectionDateFrom(), searchCriteria
						.getCollectionDateTo(), searchCriteria.getPointType(),
				searchCriteria.getTechType(), searchCriteria
						.getConstructionDateFrom(), searchCriteria
						.getConstructionDateTo());	
	}

	@Override
	public AccessPointDto getAccessPoint(Long id) {

		AccessPoint canonicalItem = aph.getAccessPoint(id,true);
		AccessPointDto apDto = AccessPointServiceSupport
				.copyCanonicalToDto(canonicalItem);
		
		return apDto;
	}

	@Override
	public AccessPointDto saveAccessPoint(AccessPointDto accessPointDto) {
		return AccessPointServiceSupport.copyCanonicalToDto(aph
				.saveAccessPoint(AccessPointServiceSupport
						.copyDtoToCanonical(accessPointDto)));
	}

	@Override
	public void delete(TechnologyTypeDto item) {
		// TODO implement delete tech type
	}

	@Override
	public TechnologyTypeDto getTechnologyType(Long id) {
		// TODO implement find by id
		return null;
	}

	@Override
	public List<TechnologyTypeDto> list() {
		// TODO implement list tech type
		return null;
	}

	@Override
	public TechnologyTypeDto save(TechnologyTypeDto item) {
		// TODO implement save tech type
		return null;
	}

	@Override
	public void rotateImage(String fileName) {
		String[] imageURLParts = ImageUtils.parseImageParts(fileName);
		Random rand = new Random();
		InputStream in;
		ByteArrayOutputStream out = null;
		URL url;
		byte[] newImage = null;
		try {
			url = new URL(fileName + "?random=" + rand.nextInt());
			in = url.openStream();
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int size;

			while ((size = in.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "Could not rotate image", e);
		}

		GAEImageAdapter gaeImg = new GAEImageAdapter();
		newImage = gaeImg.rotateImage(out.toByteArray(), 90);
		S3Driver s3 = new S3Driver();
		if (this.getUploadS3Flag()) {
			try {

				s3.uploadFile(PropertyUtil.getProperty(S3_BUCKET), imageURLParts[1] + imageURLParts[2],
						newImage);
			} catch (Exception ex) {
				// This is here for dev env where you can't make S3 puts
				log.info(ex.getMessage());

			}
		}		
	}

	/**
	 * returns an array of AccessPointDto objects that match the criteria passed
	 * in.
	 */
	public AccessPointDto[] listAccessPointByLocation(String country,
			String community, String type) {
		AccessPointDao apDAO = new AccessPointDao();
		List<AccessPoint> summaries = apDAO.listAccessPointByLocation(country,
				community, type, null, "all");
		AccessPointDto[] dtoList = null;

		if (summaries != null) {

			dtoList = new AccessPointDto[summaries.size()];

			for (int i = 0; i < summaries.size(); i++) {
				AccessPointDto dto = AccessPointServiceSupport
						.copyCanonicalToDto(summaries.get(i));
				dtoList[i] = dto;
			}
		}
		return dtoList;
	}

	private String accessPointCursor = null;

	@Override
	public String getCursorString() {
		return accessPointCursor;
	}

	@Override
	public ResponseDto<ArrayList<AccessPointDto>> listErrorAccessPoints(
			String cursorString) {
		AccessPointDao apDao = new AccessPointDao();
		ArrayList<AccessPointDto> apDtoList = new ArrayList<AccessPointDto>();
		List<AccessPoint> pointList = apDao
				.listAccessPointsWithErrors(cursorString);
		for (AccessPoint apItem : pointList) {
			AccessPointDto apDto = AccessPointServiceSupport
					.copyCanonicalToDto(apItem);
			apDtoList.add(apDto);
		}
		ResponseDto<ArrayList<AccessPointDto>> container = new ResponseDto<ArrayList<AccessPointDto>>();
		container.setCursorString(AccessPointDao.getCursor(pointList));
		container.setPayload(apDtoList);

		return container;
	}

	@Override
	public List<String> listCountryCodes() {
		CountryDao countryDao = new CountryDao();
		List<String> countryCodesList = new ArrayList<String>();
		for (Country item : countryDao.list("isoAlpha2Code", "asc", "all")) {
			countryCodesList.add(item.getIsoAlpha2Code());
		}
		return countryCodesList;
	}

	private Boolean uploadS3Flag = true;

	public void setUploadS3Flag(Boolean uploadS3Flag) {
		this.uploadS3Flag = uploadS3Flag;
	}

	public Boolean getUploadS3Flag() {
		return uploadS3Flag;
	}

	@Override
	public String returnS3Path() {
		return PropertyUtil.getProperty("surveyuploadurl");
	}
}
