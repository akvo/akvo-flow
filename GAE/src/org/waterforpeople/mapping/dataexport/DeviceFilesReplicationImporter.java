package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;
import org.waterforpeople.mapping.dao.DeviceFilesDao;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;
import org.waterforpeople.mapping.domain.Status.StatusCode;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dataexport.applet.DataImporter;
import com.google.appengine.api.datastore.Text;

public class DeviceFilesReplicationImporter implements DataImporter {

	@Override
	public Map<Integer, String> validate(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeImport(File file, String serverBase) {

	}

	@Override
	public void executeImport(String sourceBase, String serverBase) {
		DeviceFilesDao dfDao = new DeviceFilesDao();
		for (DeviceFiles df : fetchDeviceFiles(
				StatusCode.PROCESSED_WITH_ERRORS, sourceBase)) {
			DeviceFiles dfLocal = new DeviceFiles();
			dfLocal.setURI(df.getURI());
			dfLocal.setPhoneNumber(df.getPhoneNumber());
			dfLocal.setProcessedStatus(df.getProcessedStatus());
			dfLocal.setProcessingMessageText(df.getProcessingMessageText());
			dfLocal.setProcessDate(df.getProcessDate());
			dfDao.save(dfLocal);
		}

	}

	private List<DeviceFiles> fetchDeviceFiles(StatusCode statusCode,
			String serverBase) {
		List<DeviceFilesDto> dtoList = null;
		List<DeviceFiles> canonicalList = new ArrayList<DeviceFiles>();
		try {
			dtoList = BulkDataServiceClient.fetchDeviceFiles(
					statusCode.toString(), serverBase);
		} catch (IOException iex) {
			iex.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
		if (dtoList != null)
			return copyAndCreateList(canonicalList, dtoList);
		else
			return null;
	}

	public static List<DeviceFiles> copyAndCreateList(
			List<DeviceFiles> canonicalList, List<DeviceFilesDto> dtoList) {
		String surveyDtoStatus = null;

		for (DeviceFilesDto dto : dtoList) {
			DeviceFiles canonical = null;
			canonical = new DeviceFiles();
			canonical.setChecksum(dto.getChecksum());
			canonical.setPhoneNumber(dto.getPhoneNumber());
			canonical.setProcessDate(dto.getProcessDate());
			canonical.setProcessingMessageText(new Text(dto
					.getProcessingMessage()));
			if (dto.getProcessedStatus() != null)
				canonical.setProcessedStatus(StatusCode.valueOf(dto
						.getProcessedStatus()));
			else
				canonical.setProcessedStatus(null);
			canonical.setURI(dto.getURI());
			canonicalList.add(canonical);
		}
		return canonicalList;
	}

}
