/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;






import org.waterforpeople.mapping.app.util.json.JSONObject;
//import org.json.JSONArray;
//import org.json.JSONObject;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleResponse;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleSummaryDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.gallatinsystems.surveyal.domain.SurveyedLocaleSummary;

/**
 * JSON service for returning the list of records for a specific surveyId
 * 
 * @author Mark Tiele Westra
 */
public class SurveyedLocaleServlet extends AbstractRestApiServlet {
	private static final long serialVersionUID = 8748650927754433019L;
	private SurveyedLocaleDao surveyedLocaleDao;

	public SurveyedLocaleServlet() {
		setMode(JSON_MODE);
		surveyedLocaleDao = new SurveyedLocaleDao();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SurveyedLocaleRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	/**
	 * calls the surveyedLocaleDao to get the list of surveyedLocales for a
	 * certain surveyGroupId passed in via the request, or the total number of
	 * available surveyedLocales if the checkAvailable flag is set.
	 */
	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		SurveyedLocaleRequest slReq = (SurveyedLocaleRequest) req;

		SurveyedLocaleResponse resp = new SurveyedLocaleResponse();

		if (slReq.getCheckAvailable()) {
			SurveyedLocaleSummaryDao SLSdao = new SurveyedLocaleSummaryDao();
			SurveyedLocaleSummary SLSummary = SLSdao.getBySurveyGroupId(slReq.getSurveyGroupId());
			if (SLSummary != null && SLSummary.getKey() != null) {
				resp.setSurveyedLocaleCount(SLSummary.getCount());
			} else {
				resp.setSurveyedLocaleCount(0L);
			}
			return resp;
		}

		List<SurveyedLocale> results = surveyedLocaleDao
				.listLocalesBySurveyGroupId(slReq.getSurveyGroupId());
		// we probably want to include a cursor at some point:
				//.listLocalesBySurveyGroupId(slReq.getSurveyGroupId(),slReq.getCursor());

		return convertToResponse(results, slReq.getSurveyGroupId(), SurveyedLocaleDao.getCursor(results));
	}

	/**
	 * converts the domain objects to dtos and then installs them in a
	 * RecordDataResponse object
	 */
	protected SurveyedLocaleResponse convertToResponse(List<SurveyedLocale> slList, Long surveyGroupId,
			String cursor) {
		SurveyedLocaleResponse resp = new SurveyedLocaleResponse();
		SurveyedLocaleDao slDao = new SurveyedLocaleDao();
		if (slList != null) {
			List<SurveyedLocaleDto> dtoList = new ArrayList<SurveyedLocaleDto>();
			
			// for each surveyedLocale, get the surveyalValues and store them in a map
			for (SurveyedLocale sl : slList){
				List<SurveyalValue> svList = slDao.listValuesByLocale(sl.getKey().getId());
				HashMap<Long,List<SurveyalValue>> instanceMap = new HashMap<Long,List<SurveyalValue>>();
				if (svList != null && svList.size() > 0){
					for (SurveyalValue sv : svList){
						//put them in a map with the surveyInstance as key
						if (instanceMap.containsKey(sv.getSurveyInstanceId())){
							instanceMap.get(sv.getSurveyInstanceId()).add(sv);
						} else {
							instanceMap.put(sv.getSurveyInstanceId(),new ArrayList<SurveyalValue>());
							instanceMap.get(sv.getSurveyInstanceId()).add(sv);
						}
					}
				}
				// put them in the dto
				SurveyedLocaleDto dto = new SurveyedLocaleDto();
				dto.setId(sl.getIdentifier());
				dto.setLat(sl.getLatitude());
				dto.setLon(sl.getLongitude());
				SurveyInstanceDAO sDao = new SurveyInstanceDAO();
				for (Long instanceId : instanceMap.keySet()){
					SurveyInstanceDto siDto = new SurveyInstanceDto();
					SurveyInstance si = sDao.getByKey(instanceId);
					if (si != null){
						siDto.setUuid(si.getUuid());
					}
					siDto.setCollectionDate(instanceMap.get(instanceId).get(0).getCollectionDate().getTime());
					siDto.setSurveyId(instanceMap.get(instanceId).get(0).getSurveyId());
					for (SurveyalValue sv : instanceMap.get(instanceId)){
						if (!sv.getQuestionType().equals("IMAGE")){
							siDto.addProperty(sv.getSurveyQuestionId(), sv.getStringValue() != null ? sv.getStringValue() : "");
						}
					}
					dto.getSurveyInstances().add(siDto);
				}
				dtoList.add(dto);
			}
			resp.setSurveyedLocaleData(dtoList);
		}
		return resp;
	}

	/**
	 * writes response as a JSON string
	 */
	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		SurveyedLocaleResponse slResp = (SurveyedLocaleResponse) resp;
		JSONObject result = new JSONObject(slResp);

//		// do this differently
//		if (slResp.getSurveyedLocaleCount() == null) {
//
//		..	JSONArray arr = result.getJSONArray("recordData");
////			if (arr != null) {
////				for (int i = 0; i < arr.length(); i++) {
////					((JSONObject) arr.get(i)).put("questionIds", rdResp
////							.getSurveyedLocaleData().get(i).getQuestionIds());
////
////					((JSONObject) arr.get(i)).put("answerValues", rdResp
////							.getSurveyedLocaleData().get(i).getAnswerValues());
////				}
////			}
//		//	JSONObject meta = result.getJSONObject("recordsMeta");
////			if (meta != null){
////				meta.put("questionIds", slResp.getRecordsMeta().getQuestionIds());
////				meta.put("metricIds", slResp.getRecordsMeta().getMetricIds());
////				meta.put("metricNames", slResp.getRecordsMeta().getMetricNames());
////				meta.put("includeInList", slResp.getRecordsMeta().getIncludeInList());
////			}
//		}
		getResponse().getWriter().println(result.toString());
	}
}