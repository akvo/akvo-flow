/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.akvo.flow.domain.mapper.QuestionDtoMapper;
import org.akvo.flow.domain.mapper.QuestionOptionDtoMapper;
import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;
import org.waterforpeople.mapping.app.gwt.server.surveyinstance.SurveyInstanceServiceImpl;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyRestResponse;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.metric.dao.MetricDao;
import com.gallatinsystems.metric.dao.SurveyMetricMappingDao;
import com.gallatinsystems.metric.domain.Metric;
import com.gallatinsystems.metric.domain.SurveyMetricMapping;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.dao.ScoringRuleDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Question.Type;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.ScoringRule;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.KeyFactory;

public class SurveyRestServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger.getLogger(SurveyRestServlet.class.getName());

    private static final String CHART_API_URL = "http://chart.apis.google.com/chart?chs=300x225&cht=p&chtt=";
    private static final String CHART_API_DATA_PARAM = "&chd=t:";
    private static final String CHART_API_LEGEND_PARAM = "&chdl=";

    private SurveyGroupDAO sgDao;
    private SurveyDAO surveyDao;
    private QuestionOptionDao optionDao;
    private TranslationDao translationDao;
    private ScoringRuleDao scoringRuleDao;
    private QuestionGroupDao qgDao;
    private QuestionDao qDao;
    private QuestionOptionDao qoDao;
    private SurveyQuestionSummaryDao summaryDao;
    private SurveyInstanceDAO instanceDao;

    public SurveyRestServlet() {
        setMode(JSON_MODE);
        sgDao = new SurveyGroupDAO();
        surveyDao = new SurveyDAO();
        instanceDao = new SurveyInstanceDAO();
        optionDao = new QuestionOptionDao();
        translationDao = new TranslationDao();
        scoringRuleDao = new ScoringRuleDao();
        qgDao = new QuestionGroupDao();
        qDao = new QuestionDao();
        qoDao = new QuestionOptionDao();
        summaryDao = new SurveyQuestionSummaryDao();
    }

    private static final long serialVersionUID = 1165507917062204859L;

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new SurveyRestRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        SurveyRestResponse response = new SurveyRestResponse();
        SurveyRestRequest surveyReq = (SurveyRestRequest) req;
        Boolean questionSaved = null;
        if (SurveyRestRequest.SAVE_QUESTION_ACTION
                .equals(surveyReq.getAction())) {
            questionSaved = saveQuestion(surveyReq);
            response.setCode("200");
            response.setMessage("Record Saved status: " + questionSaved);
        } else if (SurveyRestRequest.LIST_SURVEY_GROUPS_ACTION.equals(surveyReq
                .getAction())) {
            response = listSurveyGroups(surveyReq.getCursor(), response);
        } else if (SurveyRestRequest.GET_SURVEY_GROUP_ACTION.equals(surveyReq
                .getAction())) {
            List<SurveyGroupDto> sgList = new ArrayList<SurveyGroupDto>();
            Long surveyGroupId = null;

            if (surveyReq.getSurveyGroupId() != null) {
                surveyGroupId = surveyReq.getSurveyGroupId();
            } else if (surveyReq.getSurveyId() != null) {
                Survey s = surveyDao.getById(surveyReq.getSurveyId());
                if (s != null) {
                    surveyGroupId = s.getSurveyGroupId();
                }
            }

            if (surveyGroupId != null) {
                SurveyGroup sg = sgDao.getByKey(surveyGroupId);
                if (sg != null) {
                    sgList.add(new SurveyGroupDto(sg));
                }
            }
            response.setDtoList(sgList);
        } else if (SurveyRestRequest.LIST_SURVEYS_ACTION.equals(surveyReq
                .getAction())) {
            response = listSurveys(surveyReq.getSurveyGroupId(),
                    surveyReq.getCursor(), response);
        } else if (SurveyRestRequest.GET_SURVEY_ACTION.equals(surveyReq
                .getAction())) {
            List<SurveyDto> sDtoList = new ArrayList<SurveyDto>();
            sDtoList.add(getSurvey(new Long(surveyReq.getSurveyId())));
            response.setDtoList(sDtoList);
        } else if (SurveyRestRequest.LIST_GROUP_ACTION.equals(surveyReq
                .getAction())
                || SurveyRestRequest.LIST_QUESTION_GROUP_ACTION
                        .equals(surveyReq.getAction())) {
            response.setDtoList(listQuestionGroups(new Long(surveyReq
                    .getSurveyId())));
        } else if (SurveyRestRequest.LIST_QUESTION_ACTION.equals(surveyReq.getAction())) {
            response.setDtoList(listGroupQuestionsWithLevelNames(new Long(surveyReq.getQuestionGroupId())));
        } else if (SurveyRestRequest.LIST_SURVEY_QUESTIONS_ACTION.equals(surveyReq.getAction())) {
            response.setDtoList(listSurveyQuestionsWithLevelNames(new Long(surveyReq.getSurveyId())));
        } else if (SurveyRestRequest.LIST_QUESTION_OPTIONS_ACTION.equals(surveyReq.getAction())) {
            response.setDtoList(listQuestionOptions(surveyReq.getQuestionId()));
        } else if (SurveyRestRequest.LIST_SURVEY_QUESTION_OPTIONS_ACTION.equals(surveyReq.getAction())) {
            response.setDtoList(listSurveyQuestionOptions(surveyReq.getSurveyId()));
        } else if (SurveyRestRequest.GET_SUMMARY_ACTION.equals(surveyReq
                .getAction())) {
            response.setDtoList(listSummaries(new Long(surveyReq
                    .getQuestionId())));
        } else if (SurveyRestRequest.GET_QUESTION_DETAILS_ACTION
                .equals(surveyReq.getAction())) {
            QuestionDto dto = loadQuestionDetails(new Long(
                    surveyReq.getQuestionId()));
            List<BaseDto> dtoList = new ArrayList<BaseDto>();
            dtoList.add(dto);
            response.setDtoList(dtoList);
        } else if (SurveyRestRequest.GET_SURVEY_INSTANCE_ACTION
                .equals(surveyReq.getAction())) {
            SurveyInstanceDto dto = findSurveyInstance(surveyReq
                    .getInstanceId());
            List<BaseDto> dtoList = new ArrayList<BaseDto>();
            dtoList.add(dto);
            response.setDtoList(dtoList);
        } else if (SurveyRestRequest.DELETE_SURVEY_INSTANCE.equals(surveyReq
                .getAction())) {
            SurveyInstanceService sis = new SurveyInstanceServiceImpl();
            sis.deleteSurveyInstance(surveyReq.getInstanceId());
        } else if (SurveyRestRequest.GET_GRAPH_ACTION.equals(surveyReq
                .getAction())) {
            response.setUrl(constructChartUrl(surveyReq.getQuestionId(),
                    surveyReq.getGraphType()));
        } else if (SurveyRestRequest.UPDATE_QUESTION_ORDER_ACTION
                .equals(surveyReq.getAction())) {
            Question q = new Question();
            q.setKey(KeyFactory.createKey("Question", surveyReq.getQuestionId()));
            q.setOrder(surveyReq.getQuestionOrder());
            List<Question> questionList = new ArrayList<Question>();
            questionList.add(q);
            qDao.updateQuestionOrder(questionList);
        }
        return response;
    }

    /**
     * constructs a Google Charts API url for creating an image chart using the data in the data
     * store for the selected question TODO: support other graph types. Right now, we always return
     * pie charts
     *
     * @param questionId
     * @param graphType
     * @return
     */
    private String constructChartUrl(Long questionId, String graphType) {
        StringBuilder url = new StringBuilder(CHART_API_URL);
        SurveyQuestionSummaryDao summaryDao = new SurveyQuestionSummaryDao();
        List<SurveyQuestionSummary> summaries = summaryDao
                .listByQuestion(questionId.toString());
        Question q = qDao.getByKey(questionId);
        if (q != null && summaries != null) {
            url.append(q.getText()).append(CHART_API_LEGEND_PARAM);
            StringBuilder legend = new StringBuilder();
            StringBuilder data = new StringBuilder();
            int i = 0;
            for (SurveyQuestionSummary sum : summaries) {
                if (i > 0) {
                    legend.append("|");
                    data.append(",");
                }
                legend.append(sum.getResponse());
                data.append(sum.getCount());
            }
            url.append(legend.toString()).append(CHART_API_DATA_PARAM)
                    .append(data.toString());
        }
        return url.toString();
    }

    private SurveyRestResponse listSurveys(Long surveyGroupId,
            String cursorString, SurveyRestResponse response) {
        SurveyDAO sDao = new SurveyDAO();
        List<Survey> groups = sDao.listSurveysByGroup(surveyGroupId);
        List<SurveyDto> dtoList = new ArrayList<SurveyDto>();
        cursorString = SurveyDAO.getCursor(groups);
        if (groups != null) {
            for (Survey s : groups) {
                SurveyDto dto = new SurveyDto();
                DtoMarshaller.copyToDto(s, dto);

                // due to difference in property names between Survey and SurveyDto
                dto.setDescription(s.getDesc());
                dtoList.add(dto);
            }
        }
        response.setDtoList(dtoList);
        response.setCursor(cursorString);
        return response;
    }

    private SurveyDto getSurvey(Long surveyId) {
        SurveyDAO surveyDao = new SurveyDAO();
        SurveyDto dto = new SurveyDto();
        Survey s = surveyDao.getById(surveyId);
        DtoMarshaller.copyToDto(s, dto);

        // difference in property names between Survey and SurveyDto
        dto.setDescription(s.getDesc());

        return dto;
    }

    /**
     * sets the http code to success and writes the RestResponse as a new JSON object to the
     * response output stream
     */
    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
        new ObjectMapper().writeValue(getResponse().getWriter(), resp);

    }

    /**
     * gets all questionGroups for a given survey
     *
     * @param surveyId
     * @return
     */
    private List<QuestionGroupDto> listQuestionGroups(Long surveyId) {
        TreeMap<Integer, QuestionGroup> groups = qgDao
                .listQuestionGroupsBySurvey(surveyId);
        List<QuestionGroupDto> dtoList = new ArrayList<QuestionGroupDto>();
        if (groups != null) {
            for (QuestionGroup q : groups.values()) {
                QuestionGroupDto dto = new QuestionGroupDto();
                DtoMarshaller.copyToDto(q, dto);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    /**
     * gets all surveyGroups for a given survey
     *
     * @param surveyId
     * @return
     */
    private SurveyRestResponse listSurveyGroups(String cursorString,
            SurveyRestResponse response) {
        SurveyGroupDAO sgDao = new SurveyGroupDAO();
        List<SurveyGroup> groups = sgDao.list(cursorString);
        List<SurveyGroupDto> dtoList = new ArrayList<SurveyGroupDto>();
        cursorString = SurveyGroupDAO.getCursor(groups);
        if (groups != null) {
            for (SurveyGroup sg : groups) {
                SurveyGroupDto dto = new SurveyGroupDto();
                DtoMarshaller.copyToDto(sg, dto);
                dtoList.add(dto);
            }
        }
        response.setDtoList(dtoList);
        response.setCursor(cursorString);
        return response;
    }

    /**
     * gets the full details of the base surveyInstance object (no answers)
     *
     * @param surveyInstanceId
     * @return
     */
    private SurveyInstanceDto findSurveyInstance(Long surveyInstanceId) {
        SurveyInstance instance = instanceDao.getByKey(surveyInstanceId);
        SurveyInstanceDto dto = null;
        if (instance != null) {
            dto = new SurveyInstanceDto();
            DtoMarshaller.copyToDto(instance, dto);

            SurveyedLocaleDao slDao = new SurveyedLocaleDao();
            SurveyedLocale sl = null;
            if (instance.getSurveyedLocaleId() != null) {
                sl = slDao.getById(instance.getSurveyedLocaleId());
            }
            if (sl != null) {
                dto.setSurveyedLocaleIdentifier(sl.getIdentifier() == null ? "" : sl
                        .getIdentifier());
                dto.setSurveyedLocaleDisplayName(sl.getDisplayName() == null ? "" : sl
                        .getDisplayName());
            } else {
                dto.setSurveyedLocaleIdentifier("");
                dto.setSurveyedLocaleDisplayName("");
            }

        }
        return dto;
    }

    /**
     * lists questions
     * @param questions
     * @return
     */
    private List<QuestionDto> listQuestions(Collection<Question> questions) {
        List<QuestionDto> dtoList = new ArrayList<QuestionDto>();
        QuestionDtoMapper mapper = new QuestionDtoMapper();
        if (questions != null) {
            for (Question q : questions) {
                dtoList.add(mapper.transform(q));
            }
        }
        return dtoList;
        
    }
    
    /**
     * add cascade level names to a list
     * @param questions
     * @return
     */
    private void addLevelNames(List<QuestionDto> qlList) {
        for (QuestionDto q : qlList) {
            if (q.getType().equals(QuestionDto.QuestionType.CASCADE) && q.getCascadeResourceId() != null) {
                CascadeResource cr =
                        new CascadeResourceDao().getByKey(q.getCascadeResourceId());
                if (cr != null) {
                    q.setLevelNames(cr.getLevelNames());
                }
            }
        }
    }
    
    /**
     * lists all questions for a given questionGroup
     *
     * @param groupId
     * @return
     */
    private List<QuestionDto> listGroupQuestionsWithLevelNames(Long groupId) {
        List<QuestionDto> qlList = listQuestions(qDao.listQuestionsByQuestionGroup(groupId, false).values());
        addLevelNames(qlList);
        return qlList;
    }

    /**
     * lists all questions for a given survey
     *
     * @param surveyId
     * @return
     */
    private List<QuestionDto> listSurveyQuestionsWithLevelNames(Long surveyId) {
        List<QuestionDto> qlList =  listQuestions(qDao.listQuestionsBySurvey(surveyId)); //useless ordering
        addLevelNames(qlList);
        return qlList;
    }

    /**
     * lists all options for a given question
     *
     * @param questionId
     * @return
     */
    private List<QuestionOptionDto> listQuestionOptions(Long questionId) {

        List<QuestionOption> options = qoDao.listByQuestionId(questionId);
        List<QuestionOptionDto> dtoList = new ArrayList<>();
        QuestionOptionDtoMapper mapper = new QuestionOptionDtoMapper();
        if (options != null) {
            for (QuestionOption option : options) {
                dtoList.add(mapper.transform(option));
            }
        }

        return dtoList;
    }

    /**
     * lists all question options in the entire survey
     * @param surveyId
     * @return
     */
    private List<QuestionOptionDto> listSurveyQuestionOptions(Long surveyId) {

        List<QuestionOptionDto> dtoList = new ArrayList<>();
        List<Question> questions = qDao.listQuestionsInOrder(surveyId, Type.OPTION);
        QuestionOptionDtoMapper mapper = new QuestionOptionDtoMapper();
        for (Question question : questions) {
            List<QuestionOption> options = qoDao.listByQuestionId(question.getKey().getId());
            if (options != null) {
                for (QuestionOption option : options) {
                    dtoList.add(mapper.transform(option));
                }
            }
        }
        return dtoList;
    }

    /**
     * loads all details (dependency, translation, options, etc) for a single question
     *
     * @param questionId
     * @return
     */
    private QuestionDto loadQuestionDetails(Long questionId) {
        Question q = qDao.getByKey(questionId, true);
        QuestionDto result = null;
        if (q != null) {
            result = SurveyServiceImpl.marshalQuestionDto(q);
        }
        return result;
    }

    /**
     * lists all the SurveyQuestionSummary objects associated with a given questionId
     *
     * @param questionId
     * @return
     */
    private List<SurveySummaryDto> listSummaries(Long questionId) {
        List<SurveyQuestionSummary> summaries = summaryDao
                .listByQuestion(questionId.toString());
        List<SurveySummaryDto> dtoList = new ArrayList<SurveySummaryDto>();
        if (summaries != null) {
            for (SurveyQuestionSummary s : summaries) {
                SurveySummaryDto dto = new SurveySummaryDto();
                dto.setCount(s.getCount());
                dto.setResponseText(s.getResponse());
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    private Boolean saveQuestion(SurveyRestRequest req)
            throws UnsupportedEncodingException {
        // temp fix until we put a validation rule in
        String questionText = req.getQuestionText();
        if (questionText.length() > 499) {
            questionText = questionText.substring(0, 499);
        }

        // TODO: Change Impl Later if we support multiple langs
        String surveyName = parseLangMap(req.getSurveyName()).get("en");
        String questionGroupName = parseLangMap(req.getQuestionGroupName())
                .get("en");

        SurveyGroup sg = null;
        String surveyGroupName = req.getSurveyGroupName();
        if (surveyGroupName != null) {
            sg = sgDao.findBySurveyGroupName(surveyGroupName);
        }

        if (sg == null) {
            sg = new SurveyGroup();
            sg.setCode(surveyGroupName);
            sgDao.save(sg);
        }

        Survey survey = null;
        String surveyPath = surveyGroupName;
        // survey = surveyDao.getByPath(surveyName, surveyPath);
        survey = surveyDao
                .getByParentIdAndCode(surveyName, sg.getKey().getId());

        if (survey == null) {
            survey = new Survey();
            survey.setName(surveyName);
            survey.setPath(surveyPath);
            survey.setCode(surveyName);
            survey.setSurveyGroupId(sg.getKey().getId());
            surveyDao.save(survey);
        }

        QuestionGroup qg = null;
        String qgPath = surveyGroupName + "/" + surveyName;
        // qg = qgDao.getByPath(questionGroupName, qgPath);
        qg = qgDao.getByParentIdandCode(questionGroupName, survey.getKey()
                .getId());

        if (qg == null) {
            qg = new QuestionGroup();
            qg.setName(questionGroupName);
            qg.setCode(questionGroupName);
            qg.setPath(qgPath);
            qg.setOrder(req.getQuestionGroupOrder());
            survey.addQuestionGroup(req.getQuestionGroupOrder(), qg);
            qg.setSurveyId(survey.getKey().getId());
            qgDao.save(qg);
        }

        String questionPath = qgPath + "/" + questionGroupName;
        // Question q = qDao.getByPath(questionOrder, questionPath);
        Question q = qDao.getByQuestionGroupId(qg.getKey().getId(),
                questionText);
        Integer questionOrder = req.getQuestionOrder();
        // since questions can have the same name, it only counts as a dupe if
        // the order matches
        if (q == null || !questionOrder.equals(q.getOrder())) {
            q = new Question();
        } else {
            // if the question already exists, delete it's children so we don't
            // get duplicates
            if (Question.Type.OPTION == q.getType()
                    || Question.Type.STRENGTH == q.getType()) {
                optionDao.deleteOptionsForQuestion(q.getKey().getId());
            }
            translationDao.deleteTranslationsForParent(q.getKey().getId(),
                    ParentType.QUESTION_TEXT);
            scoringRuleDao.deleteRulesForQuestion(q.getKey().getId());
        }
        q.setText(parseLangMap(questionText).get("en"));
        q.setPath(questionPath);
        q.setOrder(questionOrder);
        q.setReferenceId(questionOrder.toString());
        q.setQuestionGroupId(qg.getKey().getId());
        q.setSurveyId(survey.getKey().getId());

        for (Map.Entry<String, String> qTextItem : parseLangMap(questionText)
                .entrySet()) {
            if (!qTextItem.getKey().equals("en")) {
                Translation t = new Translation();
                t.setLanguageCode(qTextItem.getKey());
                t.setText(qTextItem.getValue());
                t.setParentType(ParentType.QUESTION_TEXT);
                q.addTranslation(t);
            }
        }
        String questionType = req.getQuestionType();
        if (questionType.equals("GEO")) {
            q.setType(Question.Type.GEO);
        } else if (questionType.equals("FREE_TEXT")) {
            q.setType(Question.Type.FREE_TEXT);
        } else if (questionType.equals("OPTION")
                || questionType.equals("STRENGTH")) {
            q.setAllowMultipleFlag(req.getAllowMultipleFlag());
            q.setAllowOtherFlag(req.getAllowOtherFlag());
            if (questionType.equals("OPTION")) {
                q.setType(Type.OPTION);
            } else {
                q.setType(Type.STRENGTH);
            }
            int i = 1;
            for (QuestionOptionContainer qoc : parseQuestionOption(req
                    .getOptions())) {
                QuestionOption qo = new QuestionOption();
                qo.setText(qoc.getOption());
                qo.setCode(qoc.getOption());
                qo.setOrder(i++);
                if (qoc.getAltLangs() != null) {
                    for (QuestionOptionContainer altOpt : qoc.getAltLangs()) {
                        Translation t = new Translation();
                        t.setLanguageCode(altOpt.langCode);
                        t.setText(altOpt.getOption());
                        t.setParentType(ParentType.QUESTION_TEXT);
                        qo.addTranslation(t);
                    }
                }
                q.addQuestionOption(qo);
            }
        } else if (questionType.equals("PHOTO")) {
            q.setType(Question.Type.PHOTO);
        } else if (questionType.equals("NUMBER")) {
            q.setType(Question.Type.NUMBER);
            q.setAllowDecimal(req.getAllowDecimal());
            q.setAllowSign(req.getAllowSign());
            q.setMinVal(req.getMinVal());
            q.setMaxVal(req.getMaxVal());
        } else if (questionType.equals("VIDEO")) {
            q.setType(Question.Type.VIDEO);
        }

        if (req.getMandatoryFlag() != null) {
            q.setMandatoryFlag(req.getMandatoryFlag());
        }

        // deal with options and dependencies
        String dependentQuestion = req.getDependQuestion();
        if (dependentQuestion != null && dependentQuestion.trim().length() > 1) {
            String[] parts = dependentQuestion.split("\\|");
            Integer quesitonOrderId = new Integer(parts[0]);
            String answer = parts[1];
            Question dependsOnQuestion = qDao.getByGroupIdAndOrder(qg.getKey()
                    .getId(), quesitonOrderId);
            if (dependsOnQuestion != null) {
                q.setDependentFlag(true);
                q.setDependentQuestionId(dependsOnQuestion.getKey().getId());
                q.setDependentQuestionAnswer(answer);
            }

        } else {
            q.setDependentFlag(false);
        }
        q = qDao.save(q);
        if (req.getMetricName() != null
                && req.getMetricName().trim().length() > 0) {
            MetricDao metricDao = new MetricDao();
            List<Metric> metrics = metricDao.listMetrics(req.getMetricName(),
                    req.getMetricGroup(), null, null, null);
            if (metrics != null && metrics.size() > 0) {
                SurveyMetricMappingDao mappingDao = new SurveyMetricMappingDao();
                SurveyMetricMapping mapping = new SurveyMetricMapping();
                mapping.setSurveyId(q.getSurveyId());
                mapping.setQuestionGroupId(q.getQuestionGroupId());
                mapping.setSurveyQuestionId(q.getKey().getId());
                mapping.setMetricId(metrics.get(0).getKey().getId());
                mappingDao.save(mapping);
            }
        }

        // now update the question id in the children and save
        if (q.getQuestionOptionMap() != null) {
            for (QuestionOption opt : q.getQuestionOptionMap().values()) {
                opt.setQuestionId(q.getKey().getId());
                if (opt.getText() != null && opt.getText().contains(",")) {
                    opt.setText(opt.getText().replaceAll(",", "-"));
                    if (opt.getCode() != null) {
                        opt.setCode(opt.getCode().replaceAll(",", "-"));
                    }
                }
                optionDao.save(opt);
                if (opt.getTranslationMap() != null) {
                    for (Translation t : opt.getTranslationMap().values()) {
                        t.setParentId(opt.getKey().getId());
                        t.setParentType(ParentType.QUESTION_OPTION);
                        translationDao.save(t);
                    }
                }
            }
        }
        if (q.getTranslationMap() != null) {
            for (Translation t : q.getTranslationMap().values()) {
                t.setParentId(q.getKey().getId());
                t.setParentType(ParentType.QUESTION_TEXT);
                translationDao.save(t);
            }
        }
        String scoring = req.getScoring();
        if (scoring != null && scoring.trim().length() > 0
                && !"null".equalsIgnoreCase(scoring)) {
            List<ScoringRule> rules = parseScoring(scoring, q.getKey().getId());
            scoringRuleDao.save(rules);
            q.setScoringRules(rules);
        }

        qg.addQuestion(questionOrder, q);
        qgDao.save(qg);
        surveyDao.save(survey);
        sgDao.save(sg);
        log.info("Just saved " + surveyGroupName + ":" + surveyName + ":"
                + questionGroupName + ":" + questionOrder);
        return true;
    }

    private ArrayList<QuestionOptionContainer> parseQuestionOption(
            String questionOption) {
        ArrayList<QuestionOptionContainer> qoList = new ArrayList<QuestionOptionContainer>();

        String[] parts = questionOption.split("#");

        if (parts != null && parts.length == 1) {
            // if parts is only 1 then we either have 1 option with multiple
            // languages or only 1 option
            if (!parts[0].contains("|")) {
                // if there is no pipe, then it's 1 language only
                String[] options = parts[0].split(";");
                for (int i = 0; i < options.length; i++) {
                    QuestionOptionContainer opt = parseEnglishOnlyOption(options[i]
                            .trim());
                    if (opt != null) {
                        qoList.add(opt);
                    }
                }
            } else {
                // if we're here, we have only 1 option but in multiple
                // languages
                qoList.add(composeContainer(parts[0]));
            }
        } else if (parts != null) {
            for (String option : parts) {

                qoList.add(composeContainer(option));
            }
        }
        return qoList;
    }

    private QuestionOptionContainer composeContainer(String option) {
        Map<String, String> langVals = parseLangMap(option);
        String english = langVals.remove("en");
        QuestionOptionContainer container = new QuestionOptionContainer("en",
                english);
        for (Map.Entry<String, String> entry : langVals.entrySet()) {
            container.addAltLang(new QuestionOptionContainer(entry.getKey(),
                    entry.getValue()));
        }
        return container;
    }

    /**
     * constructs a translation map based on the contents of the lang param. The parameters are
     * tuples of <b>langCode|text</b> with multiple tuples separated by a ;
     *
     * @param scoringParam
     * @return
     */
    private HashMap<String, String> parseLangMap(String unparsedLangParam) {
        HashMap<String, String> langMap = new HashMap<String, String>();

        String[] parts = unparsedLangParam.split(";");
        for (String item : parts) {
            String[] langParts = item.split("\\|");
            if (langParts.length == 1) {
                // if there is no language indicator, assume it's English
                langMap.put("en", langParts[0].trim());
            } else {
                langMap.put(langParts[0].trim(), langParts[1].trim());
            }
        }
        return langMap;
    }

    /**
     * constructs a list of ScoringRules based on the contents of the scoringParam string. This
     * string is a packed-value string consisting of the following 3-tuples: <b>min|max|value</b>
     * Multiple rules are delimited by a ;
     *
     * @param scoringParam
     * @return
     */
    private List<ScoringRule> parseScoring(String scoringParam, Long questionId) {
        List<ScoringRule> rules = new ArrayList<ScoringRule>();
        String[] parts = scoringParam.split(";");
        for (String item : parts) {
            String[] ruleParts = item.split("\\|");
            // right now, we only support NUMERIC type. Change this once we
            // support other types of rules.
            if (ruleParts.length == 3) {
                rules.add(new ScoringRule(questionId, "NUMERIC", ruleParts[0],
                        ruleParts[1], ruleParts[2]));
            } else if (ruleParts.length > 3) {
                rules.add(new ScoringRule(questionId, ruleParts[0],
                        ruleParts[1], ruleParts[2], ruleParts[3]));
            } else {
                log.log(Level.WARNING, "Scoring rule cannot be parsed: "
                        + scoringParam);
            }
        }
        return rules;
    }

    /**
     * handles parsing of the "old" style question options that only have a single language. The
     * language will be defaulted to English
     *
     * @param option
     * @return
     */
    private QuestionOptionContainer parseEnglishOnlyOption(String option) {
        QuestionOptionContainer opt = null;
        String[] val = option.split("\\|");
        String value = null;
        if (val.length == 2) {
            value = val[1];
        } else if (val.length == 1) {
            value = val[0];
        }
        if (value != null) {
            opt = new QuestionOptionContainer("en", value);
        }
        return opt;
    }

    private class QuestionOptionContainer {

        private String langCode = null;
        private String option = null;
        private List<QuestionOptionContainer> altLangs;

        public QuestionOptionContainer(String langCode, String optionText) {
            this.setLangCode(langCode);
            this.setOption(optionText);
        }

        public List<QuestionOptionContainer> getAltLangs() {
            return altLangs;
        }

        public void addAltLang(QuestionOptionContainer container) {
            if (altLangs == null) {
                altLangs = new ArrayList<QuestionOptionContainer>();
            }
            altLangs.add(container);
        }

        public void setLangCode(String langCode) {
            this.langCode = langCode;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public String getOption() {
            return option;
        }
    }
}
