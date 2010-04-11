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
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import services.S3Driver;

import com.gallatinsystems.common.util.DateUtil;
import com.gallatinsystems.image.GAEImageAdapter;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AccessPointManagerServiceImpl extends RemoteServiceServlet
		implements AccessPointManagerService {

	private static final Logger log = Logger
			.getLogger(AccessPointManagerService.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 2710084399371519003L;

	@Override
	public List<AccessPointDto> listAccessPoints(
			AccessPointSearchCriteriaDto searchCriteria, Integer startRecord,
			Integer endRecord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccessPointDto> listAllAccessPoints(Integer startRecord,
			Integer endRecord) {
		List<AccessPointDto> apDtoList = new ArrayList<AccessPointDto>();

		AccessPointHelper ah = new AccessPointHelper();
		for (AccessPoint apItem : ah.listAccessPoint()) {
			AccessPointDto apDto = copyCanonicalToDto(apItem);
			apDtoList.add(apDto);
		}
		return apDtoList;
	}

	private AccessPointDto copyCanonicalToDto(AccessPoint apCanonical) {
		AccessPointDto apDto = new AccessPointDto();
		apDto.setKeyId(apCanonical.getKey().getId());
		apDto.setAltitude(apCanonical.getAltitude());
		apDto.setLatitude(apCanonical.getLatitude());
		apDto.setLongitude(apCanonical.getLongitude());
		apDto.setCommunityCode(apCanonical.getCommunityCode());
		apDto.setCollectionDate(apCanonical.getCollectionDate());
		apDto.setConstructionDate(apCanonical.getConstructionDate());
		apDto.setCountryCode(apCanonical.getCountryCode());
		apDto.setCostPer(apCanonical.getCostPer());
		apDto.setCurrentManagementStructurePoint(apCanonical
				.getCurrentManagementStructurePoint());
		apDto.setDescription(apCanonical.getDescription());
		apDto.setFarthestHouseholdfromPoint(apCanonical
				.getFarthestHouseholdfromPoint());
		apDto.setNumberOfHouseholdsUsingPoint(apCanonical
				.getNumberOfHouseholdsUsingPoint());
		apDto.setPhotoURL(apCanonical.getPhotoURL());
		apDto.setPointPhotoCaption(apCanonical.getPointPhotoCaption());
		if (apCanonical.getCollectionDate() != null) {
			apDto.setYear(DateUtil.getYear(apCanonical.getCollectionDate()));
		}
		if (apCanonical.getPointStatus() == AccessPoint.Status.FUNCTIONING_HIGH) {
			apDto.setPointStatus(AccessPointDto.Status.FUNCTIONING_HIGH);
		} else if (apCanonical.getPointStatus() == AccessPoint.Status.FUNCTIONING_OK) {
			apDto.setPointStatus(AccessPointDto.Status.FUNCTIONING_OK);
		} else if (apCanonical.getPointStatus() == AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS) {
			apDto
					.setPointStatus(AccessPointDto.Status.FUNCTIONING_WITH_PROBLEMS);
		} else if (apCanonical.getPointStatus() == AccessPoint.Status.NO_IMPROVED_SYSTEM) {
			apDto.setPointStatus(AccessPointDto.Status.NO_IMPROVED_SYSTEM);
		} else {
			apDto.setPointStatus(AccessPointDto.Status.OTHER);
			apDto.setOtherStatus(apCanonical.getOtherStatus());
		}

		if (apCanonical.getPointType() == AccessPointType.WATER_POINT) {
			apDto.setPointType(AccessPointDto.AccessPointType.WATER_POINT);
		} else {
			apDto.setPointType(AccessPointDto.AccessPointType.SANITATION_POINT);
		}

		return apDto;
	}

	private AccessPoint copyDtoToCanonical(AccessPointDto apDto) {
		AccessPoint accessPoint = new AccessPoint();
		// Check to see if it is an update or insert
		if (apDto.getKeyId() != null) {
			Key key = KeyFactory.createKey(AccessPoint.class.getSimpleName(),
					apDto.getKeyId());
			accessPoint.setKey(key);
		}
		accessPoint.setAltitude(apDto.getAltitude());
		accessPoint.setLatitude(apDto.getLatitude());
		accessPoint.setLongitude(apDto.getLongitude());
		accessPoint.setCommunityCode(apDto.getCommunityCode());
		accessPoint.setCollectionDate(apDto.getCollectionDate());
		accessPoint.setConstructionDate(apDto.getConstructionDate());
		accessPoint.setCostPer(apDto.getCostPer());
		accessPoint.setCountryCode(apDto.getCountryCode());
		accessPoint.setCurrentManagementStructurePoint(apDto
				.getCurrentManagementStructurePoint());
		accessPoint.setDescription(apDto.getDescription());
		accessPoint.setFarthestHouseholdfromPoint(apDto
				.getFarthestHouseholdfromPoint());
		accessPoint.setNumberOfHouseholdsUsingPoint(apDto
				.getNumberOfHouseholdsUsingPoint());
		accessPoint.setPhotoURL(apDto.getPhotoURL());
		accessPoint.setPointPhotoCaption(apDto.getPointPhotoCaption());
		if (apDto.getPointStatus() == AccessPointDto.Status.FUNCTIONING_HIGH) {
			accessPoint.setPointStatus(AccessPoint.Status.FUNCTIONING_HIGH);
		} else if (apDto.getPointStatus() == AccessPointDto.Status.FUNCTIONING_OK) {
			accessPoint.setPointStatus(AccessPoint.Status.FUNCTIONING_OK);
		} else if (apDto.getPointStatus() == AccessPointDto.Status.FUNCTIONING_WITH_PROBLEMS) {
			accessPoint
					.setPointStatus(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS);
		} else if (apDto.getPointStatus() == AccessPointDto.Status.NO_IMPROVED_SYSTEM) {
			accessPoint.setPointStatus(AccessPoint.Status.NO_IMPROVED_SYSTEM);
		} else {
			accessPoint.setPointStatus(AccessPoint.Status.OTHER);
			accessPoint.setOtherStatus(apDto.getOtherStatus());
		}

		if (accessPoint.getPointStatus() == Status.OTHER) {
			accessPoint.setOtherStatus(apDto.getOtherStatus());
		}
		if (apDto.getPointType() == AccessPointDto.AccessPointType.WATER_POINT) {
			accessPoint.setPointType(AccessPoint.AccessPointType.WATER_POINT);
		} else {
			accessPoint
					.setPointType(AccessPoint.AccessPointType.SANITATION_POINT);
		}
		return accessPoint;
	}

	@Override
	public Integer deleteAccessPoint(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessPointDto getAccessPoint(Long id) {
		AccessPointHelper aph = new AccessPointHelper();
		AccessPoint canonicalItem = aph.getAccessPoint(id);
		AccessPointDto apDto = copyCanonicalToDto(canonicalItem);
		return apDto;
	}

	@Override
	public AccessPointDto saveAccessPoint(AccessPointDto accessPointDto) {
		AccessPointHelper aph = new AccessPointHelper();
		return copyCanonicalToDto(aph
				.saveAccessPoint(copyDtoToCanonical(accessPointDto)));
	}

	@Override
	public void delete(TechnologyTypeDto item) {
		// TODO Auto-generated method stub

	}

	@Override
	public TechnologyTypeDto getTechnologyType(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TechnologyTypeDto> list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyTypeDto save(TechnologyTypeDto item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean rotateImage(String fileName) {
		String imageURL = fileName.substring(fileName
				.indexOf("http://waterforpeople.s3.amazonaws.com/"));
		String bucket = "waterforpeople";
		String rootURL = "http://waterforpeople.s3.amazonaws.com/";
		Random rand = new Random();
		String totalURL = imageURL + "?random=" + rand.nextInt();
		InputStream in;
		ByteArrayOutputStream out = null;
		URL url;
		try {
			url = new URL(totalURL);
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
		byte[] newImage = gaeImg.rotateImage(out.toByteArray(), 90);
		S3Driver s3 = new S3Driver();
		s3.uploadFile(bucket, imageURL, newImage);
		return null;

		/*
		 * resp.getWriter() .print( "<html><body><img src=\"" + totalURL +
		 * "\"/></body></html>");
		 */
		// serve the first image
		// resp.setHeader("Cache-Control",
		// "no-store, no-cache, must-revalidate");
		// resp.setContentType("image/jpeg");
		// resp.getOutputStream().write(newImage);

	}

	/**
	 * returns an array of AccessPointDto objects that match the criteria passed
	 * in.
	 */
	public AccessPointDto[] listAccessPointByLocation(String country,
			String community, String type) {
		AccessPointDao apDAO = new AccessPointDao();
		List<AccessPoint> summaries = apDAO.listAccessPointByLocation(country,
				community, type);
		AccessPointDto[] dtoList = null;

		if (summaries != null) {

			dtoList = new AccessPointDto[summaries.size()];

			for (int i = 0; i < summaries.size(); i++) {
				AccessPointDto dto = copyCanonicalToDto(summaries.get(i));
				dtoList[i] = dto;
			}
		}
		return dtoList;
	}
}
