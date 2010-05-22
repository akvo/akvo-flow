package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.web.dto.SurveyManagerRequest;
import org.waterforpeople.mapping.dao.SurveyContainerDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.SurveyContainer;

public class SurveyManagerServlet extends AbstractRestApiServlet {
	private static final Logger log = Logger
			.getLogger(SurveyManagerServlet.class.getName());

	public SurveyManagerServlet() {
		super();
		setMode(AbstractRestApiServlet.XML_MODE);
	}

	private static final long serialVersionUID = 4400244780977729721L;

	private String getSurveyForPhone(String devicePhoneNumber) {
		DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
		StringBuilder sb = new StringBuilder();
		for (DeviceSurveyJobQueue dsjq : dsjqDAO.get(devicePhoneNumber)) {
			sb.append(devicePhoneNumber + "," + dsjq.getSurveyID() + ","
					+ dsjq.getName() + "," + dsjq.getLanguage() + ",1.0\n");
		}
		return sb.toString();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SurveyManagerRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		// TODO: this API should be refactored to always take an ACTION
		SurveyManagerRequest mgrReq = (SurveyManagerRequest) req;
		RestResponse resp = new RestResponse();
		if (mgrReq.getAction() == null) {
			if (mgrReq.getSurveyDoc() != null) {
				SurveyDAO surveyDAO = new SurveyDAO();
//				resp.setMessage("Survey : "
//						+ surveyDAO.save(mgrReq.getSurveyDoc()));
			} else if (mgrReq.getSurveyInstanceId() != null) {
				SurveyInstanceDAO siDAO = new SurveyInstanceDAO();
				SurveyInstance si = siDAO
						.getByKey(mgrReq.getSurveyInstanceId());
				if (si != null) {
					resp.setMessage(si.toString());
				} else {
					resp.setMessage("No Survey Instance Found");
				}

			} else if (mgrReq.getSurveyId() != null) {
				SurveyContainerDao scDao = new SurveyContainerDao();
				SurveyContainer container = scDao.findBySurveyId(mgrReq
						.getSurveyId());
				if (container != null) {
					
					resp.setMessage(container.getSurveyDocument().getValue());
				} else {
					resp.setMessage("No Survey Found");
				}
			}

		} else if (SurveyManagerRequest.GET_AVAIL_DEVICE_SURVEY_ACTION
				.equalsIgnoreCase(req.getAction())) {
			if (mgrReq.getPhoneNumber() != null) {
				resp.setMessage(getSurveyForPhone(mgrReq.getPhoneNumber()));
			}
		}
		return resp;
	}

	@Override
	protected void writeOkResponse(RestResponse response) throws Exception {
		try {
			HttpServletResponse resp = getResponse();
			resp.getWriter().print(response.getMessage());
		} catch (IOException e) {
			log
					.log(
							Level.SEVERE,
							"Could not write survey manager response to http output stream",
							e);
		}
	}
}
