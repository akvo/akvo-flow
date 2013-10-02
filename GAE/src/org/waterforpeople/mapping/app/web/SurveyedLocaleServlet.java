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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleResponse;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleSummaryDao;
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
		if (slList != null) {
			List<SurveyedLocaleDto> dtoList = new ArrayList<SurveyedLocaleDto>();
			SurveyDAO sDao = new SurveyDAO();
			QuestionDao qDao = new QuestionDao();
			List<Survey> surveyList = sDao.listSurveysByGroup(surveyGroupId);
//			List<Question> QuestionList= new ArrayList<Question>();
//			// get all the questions from all the surveys in the project, and add them to surveyQuestionList
//			if (surveyList != null && surveyList.size() > 0){
//				for (Survey s : surveyList){
//					List<Question> questions = qDao.listQuestionsBySurvey(s.getKey().getId());
//					if (questions !=null){
//						QuestionList.addAll(questions);
//					}
//				}
//			}

//			// for each question which has a metric, put an item in the metaDto
//			// with questionId, metricName, metricId and includeInList
//			RecordsMetaDto metaDto = new RecordsMetaDto();
//			MetricDao mDao = new MetricDao();
//			if (QuestionList.size() > 0){
//				for (Question q : QuestionList){
//					if (q.getMetricId() != null){
//						Metric m = mDao.getByKey(q.getMetricId());
//						String mName = m != null ? m.getName() : "";
//						Long mId = m!= null ? m.getKey().getId() : null;
//						metaDto.addItem(q.getKey().getId(), mName, mId, q.getIncludeInList());
//					}
//				}
//			}

			// put all the surveyedLocales in the result dto
			SurveyedLocaleDao slDao = new SurveyedLocaleDao();
			for (SurveyedLocale sl : slList) {
				SurveyedLocaleDto dto = new SurveyedLocaleDto();
				dto.setId(sl.getIdentifier());
				dto.setLastSDate(sl.getLastSurveyedDate());
				dto.setLat(sl.getLatitude());
				dto.setLon(sl.getLongitude());

				// for each question which has a metric, get the latest surveyalValue
				// with that surveyedLocaleId and metricId and order by time desc
//				if (QuestionList.size() > 0){
//					for (Question q : QuestionList){
//						// only include questions which have a metric
//						if (q.getMetricId() != null){
//							List<SurveyalValue> sv = slDao.listValuesByLocaleAndMetric(sl.getKey().getId(),q.getMetricId());
//							if (sv != null && sv.size() > 0) {
//								if (!sv.get(0).getQuestionType().equals("GEO")
//										&& !sv.get(0).getQuestionType().equals("IMAGE")) {
//									dto.addProperty(
//										sv.get(0).getSurveyQuestionId(),
//										sv.get(0).getStringValue() != null ? sv.get(0).getStringValue() : "");
//								}
//							}
//						}
//					}
//				}
				dtoList.add(dto);
			}
			resp.setSurveyedLocaleData(dtoList);
			//resp.setRecordsMeta(metaDto);
		}
		resp.setCursor(cursor);
		return resp;
	}

	/**
	 * writes response as a JSON string
	 */
	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		SurveyedLocaleResponse slResp = (SurveyedLocaleResponse) resp;
		JSONObject result = new JSONObject(slResp, false);

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