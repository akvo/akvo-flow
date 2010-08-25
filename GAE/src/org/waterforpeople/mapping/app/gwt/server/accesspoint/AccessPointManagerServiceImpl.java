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

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.image.GAEImageAdapter;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AccessPointManagerServiceImpl extends RemoteServiceServlet
		implements AccessPointManagerService {

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
				searchCriteria.getOrderByDir(), cursorString);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessPointDto getAccessPoint(Long id) {

		AccessPoint canonicalItem = aph.getAccessPoint(id);
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
		Random rand = new Random();
		InputStream in;
		ByteArrayOutputStream out = null;
		URL url;
		try {
			url = new URL(imageURL + "?random=" + rand.nextInt());
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
		try {
			s3.uploadFile(bucket, imageURL, newImage);
		} catch (Exception ex) {
			// This is here for dev env where you can't make S3 puts
			log.info(ex.getMessage());

		}
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
	public ResponseDto<ArrayList<AccessPointDto>> listErrorAccessPoints(String cursorString) {
		AccessPointDao apDao = new AccessPointDao();
		ArrayList<AccessPointDto> apDtoList = new ArrayList<AccessPointDto>();
		List<AccessPoint> pointList =   apDao
		.listAccessPointsWithErrors(cursorString);
		for (AccessPoint apItem :pointList) {
			AccessPointDto apDto = AccessPointServiceSupport
					.copyCanonicalToDto(apItem);
			apDtoList.add(apDto);
		}
		ResponseDto<ArrayList<AccessPointDto>> container = new ResponseDto<ArrayList<AccessPointDto>>();
		container.setCursorString(AccessPointDao.getCursor(pointList));
		container.setPayload(apDtoList);

		return container;
	}
}
