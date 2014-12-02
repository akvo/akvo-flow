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

package org.waterforpeople.mapping.app.gwt.server.survey;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.BootstrapGeneratorRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyAssemblyRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyTaskRequest;
import org.waterforpeople.mapping.dao.SurveyContainerDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.exceptions.IllegalDeletionException;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;
import com.gallatinsystems.metric.dao.SurveyMetricMappingDao;
import com.gallatinsystems.metric.domain.SurveyMetricMapping;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionHelpMediaDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;
import com.gallatinsystems.survey.domain.xml.AltText;
import com.gallatinsystems.survey.domain.xml.Dependency;
import com.gallatinsystems.survey.domain.xml.Heading;
import com.gallatinsystems.survey.domain.xml.Help;
import com.gallatinsystems.survey.domain.xml.ObjectFactory;
import com.gallatinsystems.survey.domain.xml.Option;
import com.gallatinsystems.survey.domain.xml.Options;
import com.gallatinsystems.survey.domain.xml.Text;
import com.gallatinsystems.survey.domain.xml.ValidationRule;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyServiceImpl extends RemoteServiceServlet implements
        SurveyService {

    public static final String FREE_QUESTION_TYPE = "free";
    public static final String OPTION_QUESTION_TYPE = "option";
    public static final String GEO_QUESTION_TYPE = "geo";
    public static final String VIDEO_QUESTION_TYPE = "video";
    public static final String PHOTO_QUESTION_TYPE = "photo";
    public static final String SCAN_QUESTION_TYPE = "scan";
    public static final String STRENGTH_QUESTION_TYPE = "strength";
    private static final String SURVEY_S3_PROP = "surveyuploadurl";
    private static final String SURVEY_DIR_PROP = "surveyuploaddir";
    private static final String PUB_CACHE_PREFIX = "pubsrv";
    private static final String SURVEY_UPDATE_MESSAGE_ACTION = "surveyUpdate";
    private static final String SURVEY_CHANGE_COMPLTE_MESSAGE_ACTION = "surveyChangeComplete";
    private static final String SURVEY_UPDATE_MESSAGE = "Survey has been updated. Please publish it to release it to devices.";
    private static final String SURVEY_CHANGE_COMPLETE_MESSAGE = "Survey changes have been marked as complete. Please publish it to release it to devices.";
    private static final int CACHE_EXPIRY_DEFAULT = 3600;
    private static final String CACHE_EXP_PROP = "cacheExpirySeconds";

    private static final Logger log = Logger.getLogger(SurveyServiceImpl.class
            .getName());

    private static final long serialVersionUID = 5557965649047558451L;
    private SurveyDAO surveyDao;
    private Cache cache;
    private MessageDao messageDao;
    private int cacheExpirySec = CACHE_EXPIRY_DEFAULT;

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public SurveyServiceImpl() {
        surveyDao = new SurveyDAO();
        messageDao = new MessageDao();
        try {
            CacheFactory cacheFactory = CacheManager.getInstance()
                    .getCacheFactory();

            Map configMap = new HashMap();
            String cacheExpString = PropertyUtil.getProperty(CACHE_EXP_PROP);
            if (cacheExpString != null) {
                try {
                    cacheExpirySec = Integer.parseInt(cacheExpString);
                } catch (Exception e) {
                    // no-op
                }
            }
            if (cacheExpirySec <= 0) {
                cacheExpirySec = CACHE_EXPIRY_DEFAULT;
            }
            configMap.put(GCacheFactory.EXPIRATION_DELTA, cacheExpirySec);
            configMap.put(MemcacheService.SetPolicy.SET_ALWAYS, true);
            cache = cacheFactory.createCache(configMap);
        } catch (CacheException e) {
            log.log(Level.SEVERE, "Could not initialize cache", e);
        }
    }

    @Override
    public SurveyDto[] listSurvey() {

        List<Survey> surveys = surveyDao.list(Constants.ALL_RESULTS);
        SurveyDto[] surveyDtos = null;
        if (surveys != null) {
            surveyDtos = new SurveyDto[surveys.size()];
            for (int i = 0; i < surveys.size(); i++) {
                SurveyDto dto = new SurveyDto();
                Survey s = surveys.get(i);

                dto.setName(s.getName());
                dto.setVersion(s.getVersion() != null ? s.getVersion()
                        .toString() : "");
                dto.setKeyId(s.getKey().getId());
                surveyDtos[i] = dto;
            }
        }
        return surveyDtos;
    }

    @Override
    public ResponseDto<ArrayList<SurveyGroupDto>> listSurveyGroups(
            String cursorString, Boolean loadSurveyFlag,
            Boolean loadQuestionGroupFlag, Boolean loadQuestionFlag) {
        ResponseDto<ArrayList<SurveyGroupDto>> response = new ResponseDto<ArrayList<SurveyGroupDto>>();
        ArrayList<SurveyGroupDto> surveyGroupDtoList = new ArrayList<SurveyGroupDto>();
        SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();
        List<SurveyGroup> groupList = surveyGroupDao.list(cursorString);
        for (SurveyGroup canonical : groupList) {
            SurveyGroupDto dto = new SurveyGroupDto();
            DtoMarshaller.copyToDto(canonical, dto);
            dto.setSurveyList(null);
            if (canonical.getSurveyList() != null
                    && canonical.getSurveyList().size() > 0) {
                for (Survey survey : canonical.getSurveyList()) {
                    SurveyDto surveyDto = new SurveyDto();
                    DtoMarshaller.copyToDto(survey, surveyDto);
                    surveyDto.setQuestionGroupList(null);
                    if (survey.getQuestionGroupMap() != null
                            && survey.getQuestionGroupMap().size() > 0) {
                        for (QuestionGroup questionGroup : survey
                                .getQuestionGroupMap().values()) {
                            QuestionGroupDto questionGroupDto = new QuestionGroupDto();
                            DtoMarshaller.copyToDto(questionGroup,
                                    questionGroupDto);
                            if (questionGroup.getQuestionMap() != null
                                    && questionGroup.getQuestionMap().size() > 0) {
                                for (Entry<Integer, Question> questionEntry : questionGroup
                                        .getQuestionMap().entrySet()) {
                                    Question question = questionEntry
                                            .getValue();
                                    Integer order = questionEntry.getKey();
                                    QuestionDto questionDto = new QuestionDto();
                                    DtoMarshaller.copyToDto(question,
                                            questionDto);
                                    questionGroupDto.addQuestion(questionDto,
                                            order);
                                }
                            }
                            surveyDto.addQuestionGroup(questionGroupDto);
                        }
                    }
                    dto.addSurvey(surveyDto);
                }
            }
            surveyGroupDtoList.add(dto);
        }
        response.setPayload(surveyGroupDtoList);
        response.setCursorString(SurveyGroupDAO.getCursor(groupList));
        return response;
    }

    /**
     * This method will return a list of all the questions that have a specific type code
     */
    @Override
    public QuestionDto[] listSurveyQuestionByType(Long surveyId,
            QuestionType type, boolean loadTranslations) {

        QuestionDao questionDao = new QuestionDao();
        List<Question> qList = questionDao.listQuestionByType(surveyId,
                Question.Type.valueOf(type.toString()));
        QuestionDto[] dtoArr = new QuestionDto[qList.size()];
        int i = 0;
        TranslationDao transDao = new TranslationDao();
        for (Question q : qList) {
            if (loadTranslations) {
                q.setTranslationMap(transDao.findTranslations(
                        ParentType.QUESTION_TEXT, q.getKey().getId()));
            }
            dtoArr[i] = marshalQuestionDto(q);
            i++;
        }
        return dtoArr;
    }

    /**
     * lists all surveys for a group
     */
    @Override
    public ArrayList<SurveyDto> listSurveysByGroup(String surveyGroupId) {
        SurveyDAO dao = new SurveyDAO();
        List<Survey> surveys = dao.listSurveysByGroup(Long
                .parseLong(surveyGroupId));
        ArrayList<SurveyDto> surveyDtos = null;
        if (surveys != null) {
            surveyDtos = new ArrayList<SurveyDto>();
            for (Survey s : surveys) {
                SurveyDto dto = new SurveyDto();

                dto.setName(s.getName());
                dto.setVersion(s.getVersion() != null ? s.getVersion()
                        .toString() : "");
                dto.setKeyId(s.getKey().getId());
                dto.setPath(s.getPath());
                dto.setCode(s.getCode());
                dto.setPointType(s.getPointType());
                dto.setDefaultLanguageCode(s.getDefaultLanguageCode());
                if (s.getStatus() != null) {
                    dto.setStatus(s.getStatus().toString());
                }
                dto.setRequireApproval(s.getRequireApproval());
                surveyDtos.add(dto);
            }
        }
        return surveyDtos;
    }

    @Override
    public SurveyGroupDto save(SurveyGroupDto value) {
        SurveyGroupDAO sgDao = new SurveyGroupDAO();
        SurveyGroup surveyGroup = new SurveyGroup();
        DtoMarshaller.copyToCanonical(surveyGroup, value);
        surveyGroup.setSurveyList(null);
        for (SurveyDto item : value.getSurveyList()) {
            // SurveyDto item = value.getSurveyList().get(0);
            Survey survey = new Survey();
            DtoMarshaller.copyToCanonical(survey, item);
            survey.setQuestionGroupMap(null);
            int order = 1;
            for (QuestionGroupDto qgDto : item.getQuestionGroupList()) {
                QuestionGroup qg = new QuestionGroup();
                DtoMarshaller.copyToCanonical(qg, qgDto);
                survey.addQuestionGroup(order++, qg);
                int qOrder = 1;
                for (Entry<Integer, QuestionDto> qDto : qgDto.getQuestionMap()
                        .entrySet()) {
                    Question q = marshalQuestion(qDto.getValue());
                    qg.addQuestion(qOrder++, q);
                }
            }
            surveyGroup.addSurvey(survey);
        }

        DtoMarshaller.copyToDto(sgDao.save(surveyGroup), value);
        return value;
    }

    public static QuestionDto marshalQuestionDto(Question q) {
        QuestionDto qDto = new QuestionDto();

        DtoMarshaller.copyToDto(q, qDto);

        if (q.getQuestionHelpMediaMap() != null) {
            for (QuestionHelpMedia help : q.getQuestionHelpMediaMap().values()) {
                QuestionHelpDto dto = new QuestionHelpDto();
                Map<String, Translation> transMap = help.getTranslationMap();
                help.setTranslationMap(null);
                DtoMarshaller.copyToDto(help, dto);
                if (transMap != null) {
                    dto.setTranslationMap(marshalTranslations(transMap));
                }
                qDto.addQuestionHelp(dto);
            }
        }

        if (q.getQuestionOptionMap() != null) {
            OptionContainerDto ocDto = new OptionContainerDto();
            if (q.getAllowOtherFlag() != null)
                ocDto.setAllowOtherFlag(q.getAllowOtherFlag());
            if (q.getAllowMultipleFlag() != null)
                ocDto.setAllowMultipleFlag(q.getAllowMultipleFlag());
            for (QuestionOption qo : q.getQuestionOptionMap().values()) {
                QuestionOptionDto ooDto = new QuestionOptionDto();
                ooDto.setTranslationMap(marshalTranslations(qo
                        .getTranslationMap()));
                ooDto.setKeyId(qo.getKey().getId());
                if (qo.getCode() != null)
                    ooDto.setCode(qo.getCode());
                if (qo.getText() != null)
                    ooDto.setText(qo.getText());
                ooDto.setOrder(qo.getOrder());
                ocDto.addQuestionOption(ooDto);

            }
            qDto.setOptionContainerDto(ocDto);
        }

        if (q.getDependentQuestionId() != null) {
            QuestionDependencyDto qdDto = new QuestionDependencyDto();
            qdDto.setQuestionId(q.getDependentQuestionId());
            qdDto.setAnswerValue(q.getDependentQuestionAnswer());
            qDto.setQuestionDependency(qdDto);
        }

        qDto.setTranslationMap(marshalTranslations(q.getTranslationMap()));

        if (Question.Type.CASCADE.equals(q.getType()) && q.getCascadeResourceId() != null) {
            qDto.setLevelNames(getCascadeResourceLevelNames(q.getCascadeResourceId()));
        }

        return qDto;
    }

    private static List<String> getCascadeResourceLevelNames(Long id) {
        final CascadeResource cr = new CascadeResourceDao().getByKey(id);
        if (cr == null || cr.getLevelNames() == null || cr.getLevelNames().isEmpty()) {
            return null;
        }
        return cr.getLevelNames();
    }

    private static TreeMap<String, TranslationDto> marshalTranslations(
            Map<String, Translation> translationMap) {
        TreeMap<String, TranslationDto> transMap = null;
        if (translationMap != null && translationMap.size() > 0) {
            transMap = new TreeMap<String, TranslationDto>();
            for (Translation trans : translationMap.values()) {
                TranslationDto tDto = new TranslationDto();
                tDto.setKeyId(trans.getKey().getId());
                tDto.setLangCode(trans.getLanguageCode());
                tDto.setText(trans.getText());
                tDto.setParentId(trans.getParentId());
                tDto.setParentType(trans.getParentType().toString());
                transMap.put(tDto.getLangCode(), tDto);
            }
        }
        return transMap;
    }

    private static TreeMap<String, Translation> marshalFromDtoTranslations(
            Map<String, TranslationDto> translationMap) {
        TreeMap<String, Translation> transMap = null;
        if (translationMap != null && translationMap.size() > 0) {
            transMap = new TreeMap<String, Translation>();
            for (TranslationDto trans : translationMap.values()) {
                Translation t = new Translation();
                if (trans.getKeyId() != null)
                    t.setKey((KeyFactory.createKey(
                            Translation.class.getSimpleName(), trans.getKeyId())));
                t.setLanguageCode(trans.getLangCode());
                t.setText(trans.getText());
                t.setParentId(trans.getParentId());
                if (trans.getParentType().equals(
                        Translation.ParentType.QUESTION_TEXT.toString())) {
                    t.setParentType(ParentType.QUESTION_TEXT);
                } else if (trans.getParentType().equals(
                        Translation.ParentType.QUESTION_OPTION.toString())) {
                    t.setParentType(ParentType.QUESTION_OPTION);
                } else if (Translation.ParentType.QUESTION_HELP_MEDIA_TEXT
                        .toString().equals(trans.getParentType())) {
                    t.setParentType(ParentType.QUESTION_HELP_MEDIA_TEXT);
                }
                transMap.put(t.getLanguageCode(), t);
            }
        }
        return transMap;
    }

    public Question marshalQuestion(QuestionDto qdto) {
        Question q = new Question();

        DtoMarshaller.copyToCanonical(q, qdto);

        /*
         * TODO: remove as same code seems to be duplicated later in this method if
         * (qdto.getQuestionHelpList() != null) { List<QuestionHelpDto> qHListDto =
         * qdto.getQuestionHelpList(); for (QuestionHelpDto qhDto : qHListDto) { QuestionHelpMedia
         * qh = new QuestionHelpMedia(); DtoMarshaller.copyToCanonical(qh, qhDto); } }
         */

        if (qdto.getOptionContainerDto() != null) {
            OptionContainerDto ocDto = qdto.getOptionContainerDto();

            if (ocDto.getAllowOtherFlag() != null) {
                q.setAllowOtherFlag(ocDto.getAllowOtherFlag());
            }
            if (ocDto.getAllowMultipleFlag() != null) {
                q.setAllowMultipleFlag(ocDto.getAllowMultipleFlag());
            }

            if (ocDto.getOptionsList() != null) {
                ArrayList<QuestionOptionDto> optionDtoList = ocDto
                        .getOptionsList();
                for (QuestionOptionDto qoDto : optionDtoList) {
                    QuestionOption oo = new QuestionOption();
                    if (qoDto.getKeyId() != null)
                        oo.setKey((KeyFactory.createKey(
                                QuestionOption.class.getSimpleName(),
                                qoDto.getKeyId())));
                    if (qoDto.getCode() != null)
                        oo.setCode(qoDto.getCode());
                    if (qoDto.getText() != null)
                        oo.setText(qoDto.getText());
                    oo.setOrder(qoDto.getOrder());
                    // Hack
                    if (qoDto.getTranslationMap() != null) {
                        TreeMap<String, Translation> transTreeMap = SurveyServiceImpl
                                .marshalFromDtoTranslations(qoDto
                                        .getTranslationMap());

                        HashMap<String, Translation> transMap = new HashMap<String, Translation>();
                        for (Map.Entry<String, Translation> entry : transTreeMap
                                .entrySet()) {
                            transMap.put(entry.getKey(), entry.getValue());
                        }
                        oo.setTranslationMap(transMap);
                    }
                    q.addQuestionOption(oo);
                }
            }
        }

        /*
         * TODO: remove. probably not necessary as already covered by dependentFlag,
         * dependentQuestionId, and dependentQUestionAnswer members in schema if
         * (qdto.getQuestionDependency() != null) {
         * q.setDependentQuestionId(qdto.getQuestionDependency() .getQuestionId());
         * q.setDependentQuestionAnswer(qdto.getQuestionDependency() .getAnswerValue());
         * q.setDependentFlag(true); }
         */

        if (qdto.getTranslationMap() != null) {
            TreeMap<String, Translation> transMap = marshalFromDtoTranslations(qdto
                    .getTranslationMap());
            q.setTranslationMap(transMap);
        }

        if (qdto.getQuestionHelpList() != null) {
            int count = 0;
            for (QuestionHelpDto help : qdto.getQuestionHelpList()) {
                QuestionHelpMedia helpDomain = new QuestionHelpMedia();
                Map<String, TranslationDto> transMap = help.getTranslationMap();
                help.setTranslationMap(null);
                DtoMarshaller.copyToCanonical(helpDomain, help);
                if (transMap != null) {
                    helpDomain
                            .setTranslationMap(marshalFromDtoTranslations(transMap));
                }
                q.addHelpMedia(count++, helpDomain);
            }
        }

        return q;
    }

    /**
     * fully hydrates a single survey object
     */
    @Override
    public SurveyDto loadFullSurvey(Long surveyId) {
        Survey survey = surveyDao.loadFullSurvey(surveyId);
        SurveyDto dto = null;
        if (survey != null) {
            dto = new SurveyDto();
            DtoMarshaller.copyToDto(survey, dto);
            dto.setQuestionGroupList(null);
            if (survey.getQuestionGroupMap() != null) {
                ArrayList<QuestionGroupDto> qGroupDtoList = new ArrayList<QuestionGroupDto>();
                for (QuestionGroup qg : survey.getQuestionGroupMap().values()) {
                    QuestionGroupDto qgDto = new QuestionGroupDto();
                    DtoMarshaller.copyToDto(qg, qgDto);
                    qgDto.setQuestionMap(null);
                    qGroupDtoList.add(qgDto);
                    if (qg.getQuestionMap() != null) {
                        TreeMap<Integer, QuestionDto> qDtoMap = new TreeMap<Integer, QuestionDto>();
                        for (Entry<Integer, Question> entry : qg
                                .getQuestionMap().entrySet()) {
                            QuestionDto qdto = marshalQuestionDto(entry
                                    .getValue());

                            qDtoMap.put(entry.getKey(), qdto);
                        }
                        qgDto.setQuestionMap(qDtoMap);
                    }
                }
                dto.setQuestionGroupList(qGroupDtoList);
            }
        }
        return dto;
    }

    @Override
    public List<SurveyDto> listSurveysForSurveyGroup(String surveyGroupId) {
        List<Survey> surveyList = surveyDao.listSurveysByGroup(Long
                .parseLong(surveyGroupId));
        List<SurveyDto> surveyDtoList = new ArrayList<SurveyDto>();
        for (Survey canonical : surveyList) {
            SurveyDto dto = new SurveyDto();
            DtoMarshaller.copyToDto(canonical, dto);
            surveyDtoList.add(dto);
        }
        return surveyDtoList;
    }

    @Override
    public ArrayList<QuestionGroupDto> listQuestionGroupsBySurvey(
            String surveyId) {
        QuestionGroupDao questionGroupDao = new QuestionGroupDao();
        TreeMap<Integer, QuestionGroup> questionGroupList = questionGroupDao
                .listQuestionGroupsBySurvey(new Long(surveyId));
        ArrayList<QuestionGroupDto> questionGroupDtoList = new ArrayList<QuestionGroupDto>();
        for (QuestionGroup canonical : questionGroupList.values()) {
            QuestionGroupDto dto = new QuestionGroupDto();
            DtoMarshaller.copyToDto(canonical, dto);
            questionGroupDtoList.add(dto);
        }
        return questionGroupDtoList;
    }

    @Override
    public ArrayList<QuestionDto> listQuestionsByQuestionGroup(
            String questionGroupId, boolean needDetails) {
        return listQuestionsByQuestionGroup(questionGroupId, needDetails, true);
    }

    @Override
    public ArrayList<QuestionDto> listQuestionsByQuestionGroup(
            String questionGroupId, boolean needDetails,
            boolean allowSideEffects) {
        QuestionDao questionDao = new QuestionDao();
        java.util.ArrayList<QuestionDto> questionDtoList = new ArrayList<QuestionDto>();
        if (allowSideEffects) {
            TreeMap<Integer, Question> questionList = questionDao
                    .listQuestionsByQuestionGroup(
                            Long.parseLong(questionGroupId), needDetails,
                            allowSideEffects);

            if (questionList != null && !questionList.isEmpty()) {
                for (Question canonical : questionList.values()) {
                    QuestionDto dto = marshalQuestionDto(canonical);
                    questionDtoList.add(dto);
                }
            }
        } else {
            List<Question> questionList = questionDao
                    .listQuestionsInOrderForGroup(new Long(questionGroupId));
            if (questionList != null && !questionList.isEmpty()) {
                for (Question canonical : questionList) {
                    QuestionDto dto = marshalQuestionDto(canonical);
                    questionDtoList.add(dto);
                }
            }
        }
        if (questionDtoList.size() > 0) {
            return questionDtoList;
        } else {
            return null;
        }
    }

    @Override
    public String deleteQuestion(QuestionDto value, Long questionGroupId) {
        QuestionDao questionDao = new QuestionDao();
        Question canonical = new Question();
        DtoMarshaller.copyToCanonical(canonical, value);
        try {
            questionDao.delete(canonical);
        } catch (IllegalDeletionException e) {

            return e.getError();
        }
        return null;

    }

    @Override
    public QuestionDto saveQuestion(QuestionDto value, Long questionGroupId,
            boolean forceReorder) {
        QuestionDao questionDao = new QuestionDao();
        Question question = marshalQuestion(value);
        if (forceReorder) {
            TreeMap<Integer, Question> questions = questionDao
                    .listQuestionsByQuestionGroup(questionGroupId, false);
            if (questions != null) {
                for (Question q : questions.values()) {
                    if (q.getOrder() >= value.getOrder()) {
                        q.setOrder(q.getOrder() + 1);
                    }
                }
            }
        }
        question = questionDao.save(question, questionGroupId);
        saveSurveyUpdateMessage(question.getSurveyId());
        return marshalQuestionDto(question);
    }

    @Override
    public QuestionGroupDto saveQuestionGroup(QuestionGroupDto dto,
            Long surveyId) {
        QuestionGroup questionGroup = new QuestionGroup();
        DtoMarshaller.copyToCanonical(questionGroup, dto);
        QuestionGroupDao questionGroupDao = new QuestionGroupDao();
        if (questionGroup.getOrder() == null || questionGroup.getOrder() == 0) {
            Map<Integer, QuestionGroup> items = questionGroupDao
                    .listQuestionGroupsBySurvey(questionGroup.getSurveyId());
            if (items != null) {
                questionGroup.setOrder(items.size() + 1);
            } else {
                questionGroup.setOrder(1);
            }
        }
        questionGroup = questionGroupDao.save(questionGroup, surveyId);
        saveSurveyUpdateMessage(questionGroup.getSurveyId());
        DtoMarshaller.copyToDto(questionGroup, dto);
        return dto;
    }

    @Override
    public List<QuestionGroupDto> saveQuestionGroups(
            List<QuestionGroupDto> dtoList) {
        QuestionGroupDao questionGroupDao = new QuestionGroupDao();
        Set<Long> surveyIds = new HashSet<Long>();
        if (dtoList != null) {
            List<QuestionGroup> groupList = new ArrayList<QuestionGroup>();
            int i = 0;
            for (QuestionGroupDto dto : dtoList) {
                QuestionGroup questionGroup = new QuestionGroup();
                DtoMarshaller.copyToCanonical(questionGroup, dto);
                if (questionGroup.getOrder() == null
                        || questionGroup.getOrder() == 0) {
                    questionGroup.setOrder(i);
                }
                groupList.add(questionGroup);
                surveyIds.add(questionGroup.getSurveyId());
                i++;
            }
            questionGroupDao.save(groupList);
            for (int j = 0; j < groupList.size(); j++) {
                dtoList.get(j).setKeyId(groupList.get(j).getKey().getId());
            }
            for (Long surveyId : surveyIds) {
                saveSurveyUpdateMessage(surveyId);
            }
        }
        return dtoList;
    }

    @Override
    public SurveyDto saveSurvey(SurveyDto surveyDto, Long surveyGroupId) {
        Survey canonical = new Survey();
        DtoMarshaller.copyToCanonical(canonical, surveyDto);
        canonical.setStatus(Survey.Status.NOT_PUBLISHED);
        SurveyDAO surveyDao = new SurveyDAO();
        if (canonical.getKey() != null && canonical.getSurveyGroupId() == 0) {
            // fetch record from db so we don't loose assoc
            Survey sTemp = surveyDao.getByKey(canonical.getKey());
            canonical.setSurveyGroupId(sTemp.getSurveyGroupId());
            canonical.setPath(sTemp.getPath());
        }
        canonical = surveyDao.save(canonical);
        DtoMarshaller.copyToDto(canonical, surveyDto);
        saveSurveyUpdateMessage(canonical.getKey().getId());

        return surveyDto;

    }

    @Override
    public SurveyGroupDto saveSurveyGroup(SurveyGroupDto dto) {
        SurveyGroup canonical = new SurveyGroup();
        SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();
        DtoMarshaller.copyToCanonical(canonical, dto);
        canonical = surveyGroupDao.save(canonical);
        DtoMarshaller.copyToDto(canonical, dto);
        return dto;
    }

    /**
     * saves or updates a list of translation objects and returns the saved value. If the text is
     * null or blank and the ID is populated, that translation will be deleted since we shouldn't
     * allow blank translations.
     */
    @Override
    public List<TranslationDto> saveTranslations(
            List<TranslationDto> translations) {
        TranslationDao translationDao = new TranslationDao();
        List<TranslationDto> deletedItems = new ArrayList<TranslationDto>();
        Set<Long> questionIdSet = new HashSet<Long>();
        Set<Long> questionOptionIdSet = new HashSet<Long>();
        for (TranslationDto t : translations) {
            Translation transDomain = new Translation();
            // need to work around marshaller's inability to translate string to
            // enumeration values. We need to set it back after the copy call
            String parentType = t.getParentType();
            t.setParentType(null);
            DtoMarshaller.copyToCanonical(transDomain, t);
            t.setParentType(parentType);
            transDomain.setParentType(ParentType.valueOf(parentType));
            if (ParentType.QUESTION_TEXT == transDomain.getParentType()) {
                questionIdSet.add(t.getParentId());
            } else if (ParentType.QUESTION_OPTION == transDomain
                    .getParentType()) {
                questionOptionIdSet.add(t.getParentId());
            }
            transDomain.setLanguageCode(t.getLangCode());
            if (transDomain.getKey() != null
                    && (transDomain.getText() == null || transDomain.getText()
                            .trim().length() == 0)) {
                Translation itemToDelete = translationDao.getByKey(transDomain
                        .getKey());
                if (itemToDelete != null) {
                    translationDao.delete(itemToDelete);
                    deletedItems.add(t);
                }
            } else {
                transDomain = translationDao.save(transDomain);
            }
            t.setKeyId(transDomain.getKey().getId());
        }
        translations.removeAll(deletedItems);

        QuestionDao questionDao = new QuestionDao();
        for (Long optId : questionOptionIdSet) {
            QuestionOption opt = questionDao.getByKey(optId,
                    QuestionOption.class);
            if (opt != null) {
                questionIdSet.add(opt.getQuestionId());
            }
        }
        for (Long questionId : questionIdSet) {
            Question question = questionDao.getByKey(questionId);
            if (question != null) {
                saveSurveyUpdateMessage(question.getSurveyId());
            }
        }
        return translations;
    }

    @Override
    public void publishSurveyAsync(Long surveyId) {
        TaskOptions options = TaskOptions.Builder
                .withUrl("/app_worker/surveyassembly")
                .param("action", SurveyAssemblyRequest.ASSEMBLE_SURVEY)
                .param("surveyId", surveyId.toString());
        com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
                .getQueue("surveyAssembly");
        queue.add(options);
    }

    @Override
    public String publishSurvey(Long surveyId) {
        try {
            SurveyDAO surveyDao = new SurveyDAO();
            Survey survey = surveyDao.loadFullSurvey(surveyId);
            SurveyXMLAdapter sax = new SurveyXMLAdapter();
            ObjectFactory objFactory = new ObjectFactory();

            // System.out.println("XML Marshalling for survey: " + surveyId);
            com.gallatinsystems.survey.domain.xml.Survey surveyXML = objFactory
                    .createSurvey();
            ArrayList<com.gallatinsystems.survey.domain.xml.QuestionGroup> questionGroupXMLList = new ArrayList<com.gallatinsystems.survey.domain.xml.QuestionGroup>();
            for (QuestionGroup qg : survey.getQuestionGroupMap().values()) {
                // System.out.println("	QuestionGroup: " + qg.getKey().getId() +
                // ":"
                // + qg.getCode() + ":" + qg.getDescription());
                com.gallatinsystems.survey.domain.xml.QuestionGroup qgXML = objFactory
                        .createQuestionGroup();
                Heading heading = objFactory.createHeading();
                heading.setContent(qg.getCode());
                qgXML.setHeading(heading);

                // TODO: implement questionGroup order attribute
                // qgXML.setOrder(qg.getOrder());
                ArrayList<com.gallatinsystems.survey.domain.xml.Question> questionXMLList = new ArrayList<com.gallatinsystems.survey.domain.xml.Question>();
                if (qg.getQuestionMap() != null) {
                    for (Entry<Integer, Question> qEntry : qg.getQuestionMap()
                            .entrySet()) {
                        Question q = qEntry.getValue();
                        com.gallatinsystems.survey.domain.xml.Question qXML = objFactory
                                .createQuestion();
                        qXML.setId(new String("" + q.getKey().getId() + ""));
                        // ToDo fix
                        qXML.setMandatory("false");
                        if (q.getText() != null) {
                            Text text = new Text();
                            text.setContent(q.getText());
                            qXML.setText(text);
                        }
                        List<Help> helpList = new ArrayList<Help>();
                        // this is here for backward compatibility
                        if (q.getTip() != null) {
                            Help tip = new Help();
                            com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
                            t.setContent(q.getTip());
                            tip.setText(t);
                            tip.setType("tip");
                            if (q.getTip() != null
                                    && q.getTip().trim().length() > 0
                                    && !"null".equalsIgnoreCase(q.getTip()
                                            .trim())) {
                                helpList.add(tip);
                            }
                        }
                        if (q.getQuestionHelpMediaMap() != null) {
                            for (QuestionHelpMedia helpItem : q
                                    .getQuestionHelpMediaMap().values()) {
                                Help tip = new Help();
                                com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
                                t.setContent(helpItem.getText());
                                if (helpItem.getType() == QuestionHelpMedia.Type.TEXT) {
                                    tip.setType("tip");
                                } else {
                                    tip.setType(helpItem.getType().toString()
                                            .toLowerCase());
                                }
                                if (helpItem.getTranslationMap() != null) {
                                    List<AltText> translationList = new ArrayList<AltText>();
                                    for (Translation trans : helpItem
                                            .getTranslationMap().values()) {
                                        AltText aText = new AltText();
                                        aText.setContent(trans.getText());
                                        aText.setLanguage(trans
                                                .getLanguageCode());
                                        aText.setType("translation");
                                        translationList.add(aText);
                                    }
                                    if (translationList.size() > 0) {
                                        tip.setAltText(translationList);
                                    }
                                }
                                helpList.add(tip);
                            }
                        }

                        boolean hasValidation = false;
                        if (q.getIsName() != null && q.getIsName()) {
                            ValidationRule validationRule = objFactory
                                    .createValidationRule();
                            validationRule.setValidationType("name");
                            qXML.setValidationRule(validationRule);
                            hasValidation = true;

                        } else if (q.getAllowDecimal() != null
                                || q.getAllowSign() != null
                                || q.getMinVal() != null
                                || q.getMaxVal() != null) {
                            ValidationRule validationRule = objFactory
                                    .createValidationRule();
                            validationRule.setValidationType("numeric");
                            validationRule
                                    .setAllowDecimal(q.getAllowDecimal() != null ? q
                                            .getAllowDecimal().toString()
                                            .toLowerCase()
                                            : "false");
                            validationRule
                                    .setSigned(q.getAllowSign() != null ? q
                                            .getAllowSign().toString()
                                            .toLowerCase() : "false");
                            if (q.getMinVal() != null) {
                                validationRule.setMinVal(q.getMinVal()
                                        .toString());
                            }
                            if (q.getMaxVal() != null) {
                                validationRule.setMaxVal(q.getMaxVal()
                                        .toString());
                            }
                            qXML.setValidationRule(validationRule);
                            hasValidation = true;
                        }

                        if (q.getType().toString().equals(QuestionType.FREE_TEXT.toString()))
                            qXML.setType(FREE_QUESTION_TYPE);
                        else if (q.getType().toString().equals(QuestionType.GEO.toString()))
                            qXML.setType(GEO_QUESTION_TYPE);
                        else if (q.getType().toString().equals(QuestionType.NUMBER.toString())) {
                            qXML.setType(FREE_QUESTION_TYPE);
                            if (!hasValidation) {
                                ValidationRule vrule = new ValidationRule();
                                vrule.setValidationType("numeric");
                                vrule.setSigned("false");
                                qXML.setValidationRule(vrule);
                            }
                        } else if (q.getType().toString().equals(QuestionType.OPTION.toString())) {
                            qXML.setType(OPTION_QUESTION_TYPE);
                        } else if (q.getType().toString().equals(QuestionType.STRENGTH.toString())) {
                            qXML.setType(STRENGTH_QUESTION_TYPE);
                        } else if (q.getType().toString().equals(QuestionType.PHOTO.toString()))
                            qXML.setType(PHOTO_QUESTION_TYPE);
                        else if (q.getType().toString().equals(QuestionType.VIDEO.toString()))
                            qXML.setType(VIDEO_QUESTION_TYPE);
                        else if (q.getType().toString().equals(QuestionType.SCAN.toString()))
                            qXML.setType(SCAN_QUESTION_TYPE);

                        if (qEntry.getKey() != null)
                            qXML.setOrder(qEntry.getKey().toString());
                        // ToDo set dependency xml
                        Dependency dependency = objFactory.createDependency();
                        if (q.getDependentQuestionId() != null) {
                            dependency.setQuestion(q.getDependentQuestionId()
                                    .toString());
                            dependency.setAnswerValue(q
                                    .getDependentQuestionAnswer());
                            qXML.setDependency(dependency);
                        }

                        if (q.getQuestionOptionMap() != null
                                && q.getQuestionOptionMap().size() > 0) {

                            Options options = objFactory.createOptions();

                            if (q.getAllowOtherFlag() != null) {
                                options.setAllowOther(q.getAllowOtherFlag()
                                        .toString());
                            }

                            ArrayList<Option> optionList = new ArrayList<Option>();
                            for (QuestionOption qo : q.getQuestionOptionMap()
                                    .values()) {
                                Option option = objFactory.createOption();
                                Text t = new Text();
                                t.setContent(qo.getText());
                                option.addContent(t);
                                option.setValue(qo.getCode());
                                optionList.add(option);
                            }
                            options.setOption(optionList);

                            qXML.setOptions(options);
                        }
                        questionXMLList.add(qXML);
                    }
                }
                qgXML.setQuestion(questionXMLList);
                questionGroupXMLList.add(qgXML);
            }
            surveyXML.setQuestionGroup(questionGroupXMLList);
            String surveyDocument = sax.marshal(surveyXML);
            SurveyContainerDao scDao = new SurveyContainerDao();
            SurveyContainer sc = new SurveyContainer();
            sc.setSurveyId(surveyId);
            sc.setSurveyDocument(new com.google.appengine.api.datastore.Text(
                    surveyDocument));
            SurveyContainer scFound = scDao.findBySurveyId(sc.getSurveyId());
            if (scFound != null) {
                scFound.setSurveyDocument(sc.getSurveyDocument());
                scDao.save(scFound);
            } else
                scDao.save(sc);
            survey.setStatus(Survey.Status.PUBLISHED);
            surveyDao.save(survey);

        } catch (Exception ex) {
            ex.printStackTrace();
            StringBuilder sb = new StringBuilder();
            sb.append("Could not publish survey: \n cause: " + ex.getCause()
                    + " \n message" + ex.getMessage() + "\n stack trace:  ");

            return sb.toString();
        }

        return "Survey successfully published";
    }

    @Override
    public QuestionDto loadQuestionDetails(Long questionId) {
        QuestionDao questionDao = new QuestionDao();
        Question canonical = questionDao.getByKey(questionId, true);
        if (canonical != null) {
            return marshalQuestionDto(canonical);
        } else {
            return null;
        }
    }

    @Override
    public String deleteQuestionGroup(QuestionGroupDto value, Long surveyId) {
        if (value != null) {
            QuestionGroupDao qgDao = new QuestionGroupDao();
            try {
                qgDao.delete(qgDao.getByKey(value.getKeyId()));
            } catch (IllegalDeletionException e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    public String deleteSurvey(SurveyDto value, Long surveyGroupId) {
        if (value != null) {
            SurveyDAO surveyDao = new SurveyDAO();
            try {
                Survey s = new Survey();
                DtoMarshaller.copyToCanonical(s, value);
                surveyDao.delete(s);
            } catch (IllegalDeletionException e) {
                log.log(Level.SEVERE, "Could not delete survey", e);
            }
        }
        return null;
    }

    @Override
    public String deleteSurveyGroup(SurveyGroupDto value) {
        if (value != null) {
            SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();
            surveyGroupDao.delete(marshallSurveyGroup(value));
        }
        return null;
    }

    public static SurveyGroup marshallSurveyGroup(SurveyGroupDto dto) {
        SurveyGroup sg = new SurveyGroup();
        if (dto.getKeyId() != null)
            sg.setKey(KeyFactory.createKey(SurveyGroup.class.getSimpleName(),
                    dto.getKeyId()));
        if (dto.getCode() != null)
            sg.setCode(dto.getCode());
        return sg;
    }

    @Override
    public void rerunAPMappings(Long surveyId) {
        Queue queue = QueueFactory.getDefaultQueue();
        if (PropertyUtil.getProperty("domainType") != null
                && PropertyUtil.getProperty("domainType").equalsIgnoreCase(
                        "locale")) {
            log.log(Level.INFO, "Running Remap for locale");
            queue.add(TaskOptions.Builder
                    .withUrl("/app_worker/surveyalservlet")
                    .param(SurveyalRestRequest.ACTION_PARAM,
                            SurveyalRestRequest.RERUN_ACTION)
                    .param(SurveyalRestRequest.SURVEY_ID_PARAM,
                            surveyId.toString()));
        } else {
            log.log(Level.INFO, "AccessPoint");
            SurveyInstanceDAO siDao = new SurveyInstanceDAO();

            Iterable<Entity> siList = siDao.listSurveyInstanceKeysBySurveyId(surveyId);
            if (siList != null) {

                StringBuffer buffer = new StringBuffer();
                int i = 0;
                for (Entity item : siList) {
                    if (i > 0) {
                        buffer.append(",");
                    }
                    String key = item.getKey().toString();
                    Integer startPos = key.indexOf("(");
                    Integer endPos = key.indexOf(")");
                    buffer.append(key.subSequence(startPos + 1, endPos));
                    i++;
                }

                queue.add(TaskOptions.Builder
                        .withUrl("/app_worker/surveytask")
                        .param("action", "reprocessMapSurveyInstance")
                        .param(SurveyTaskRequest.ID_PARAM, surveyId.toString())
                        .param(SurveyTaskRequest.ID_LIST_PARAM,
                                buffer.toString())
                        // .param(SurveyTaskRequest.CURSOR_PARAM,
                        // SurveyInstanceDAO.getCursor(siList))
                        );

            }
        }
    }

    @Override
    public List<QuestionHelpDto> listHelpByQuestion(Long questionId) {
        QuestionHelpMediaDao helpDao = new QuestionHelpMediaDao();
        TreeMap<Integer, QuestionHelpMedia> helpMedia = helpDao
                .listHelpByQuestion(questionId);
        List<QuestionHelpDto> dtoList = new ArrayList<QuestionHelpDto>();
        if (helpMedia != null) {
            for (QuestionHelpMedia help : helpMedia.values()) {
                QuestionHelpDto dto = new QuestionHelpDto();
                Map<String, Translation> transMap = help.getTranslationMap();
                help.setTranslationMap(null);
                DtoMarshaller.copyToDto(help, dto);
                if (transMap != null) {
                    dto.setTranslationMap(marshalTranslations(transMap));
                }

                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public List<QuestionHelpDto> saveHelp(List<QuestionHelpDto> helpList) {
        QuestionHelpMediaDao helpDao = new QuestionHelpMediaDao();
        Set<Long> questionIdSet = new HashSet<Long>();
        if (helpList != null && helpList.size() > 0) {
            Collection<QuestionHelpMedia> domainList = new ArrayList<QuestionHelpMedia>();
            for (QuestionHelpDto dto : helpList) {
                QuestionHelpMedia canonical = new QuestionHelpMedia();
                DtoMarshaller.copyToCanonical(canonical, dto);
                domainList.add(canonical);
                questionIdSet.add(dto.getQuestionId());
            }
            domainList = helpDao.save(domainList);
            helpList.clear();
            for (QuestionHelpMedia domain : domainList) {
                QuestionHelpDto dto = new QuestionHelpDto();
                DtoMarshaller.copyToDto(domain, dto);
                helpList.add(dto);
            }
        }
        QuestionDao questionDao = new QuestionDao();
        for (Long questionId : questionIdSet) {
            Question question = questionDao.getByKey(questionId);
            if (question != null) {
                saveSurveyUpdateMessage(question.getSurveyId());
            }
        }
        return helpList;
    }

    @Override
    public Map<String, TranslationDto> listTranslations(Long parentId,
            String parentType) {
        TranslationDao transDao = new TranslationDao();
        Map<String, Translation> transMap = transDao.findTranslations(
                Translation.ParentType.valueOf(parentType), parentId);
        return marshalTranslations(transMap);
    }

    @Override
    public QuestionDto copyQuestion(QuestionDto existingQuestion,
            QuestionGroupDto newParentGroup) {
        Question questionToSave = marshalQuestion(existingQuestion);
        // now override all the IDs
        questionToSave.setKey(null);
        questionToSave.setPath(newParentGroup.getPath() + "/"
                + newParentGroup.getName());
        questionToSave.setQuestionGroupId(newParentGroup.getKeyId());
        if (questionToSave.getQuestionOptionMap() != null) {
            for (QuestionOption opt : questionToSave.getQuestionOptionMap()
                    .values()) {
                opt.setKey(null);
                opt.setQuestionId(null);
                if (opt.getTranslationMap() != null) {
                    for (Translation t : opt.getTranslationMap().values()) {
                        t.setKey(null);
                        t.setParentId(null);
                    }
                }
            }
        }
        if (questionToSave.getTranslationMap() != null) {
            for (Translation t : questionToSave.getTranslationMap().values()) {
                t.setParentId(null);
                t.setKey(null);
            }
        }
        if (questionToSave.getQuestionHelpMediaMap() != null) {
            for (QuestionHelpMedia help : questionToSave
                    .getQuestionHelpMediaMap().values()) {
                help.setKey(null);
                if (help.getTranslationMap() != null) {
                    for (Translation t : help.getTranslationMap().values()) {
                        t.setParentId(null);
                        t.setKey(null);
                    }
                }
            }
        }
        QuestionDao dao = new QuestionDao();
        questionToSave = dao.save(questionToSave, newParentGroup.getKeyId());
        // now see if we have a metric mapping to copy
        SurveyMetricMappingDao mappingDao = new SurveyMetricMappingDao();
        List<SurveyMetricMapping> mappings = mappingDao
                .listMappingsByQuestion(existingQuestion.getKeyId());
        if (mappings != null) {
            List<SurveyMetricMapping> newMappings = new ArrayList<SurveyMetricMapping>();
            for (SurveyMetricMapping mapping : mappings) {
                SurveyMetricMapping newMapping = new SurveyMetricMapping();
                newMapping.setQuestionGroupId(questionToSave
                        .getQuestionGroupId());
                newMapping.setMetricId(mapping.getMetricId());
                newMapping.setSurveyId(questionToSave.getSurveyId());
                newMapping.setSurveyQuestionId(questionToSave.getKey().getId());
                newMappings.add(newMapping);
            }
            if (newMappings.size() > 0) {
                mappingDao.save(newMappings);
            }
        }
        return marshalQuestionDto(questionToSave);
    }

    /**
     * updates the order for the list of questions passed in
     *
     * @param q1
     * @param q2
     * @return
     */
    @Override
    public void updateQuestionOrder(List<QuestionDto> questions) {
        if (questions != null) {
            List<Question> questionList = new ArrayList<Question>();
            for (QuestionDto qDto : questions) {
                Question q = new Question();
                q.setKey(KeyFactory.createKey(Question.class.getSimpleName(),
                        qDto.getKeyId()));
                q.setOrder(qDto.getOrder());
                questionList.add(q);
            }
            QuestionDao dao = new QuestionDao();
            dao.updateQuestionOrder(questionList);
        }
    }

    /**
     * updates the order for the list of question groups passed in
     *
     * @param q1
     * @param q2
     * @return
     */
    @Override
    public void updateQuestionGroupOrder(List<QuestionGroupDto> groups) {
        if (groups != null) {
            List<QuestionGroup> groupList = new ArrayList<QuestionGroup>();
            for (QuestionGroupDto qDto : groups) {
                QuestionGroup q = new QuestionGroup();
                q.setKey(KeyFactory.createKey(
                        QuestionGroup.class.getSimpleName(), qDto.getKeyId()));
                q.setOrder(qDto.getOrder());
                groupList.add(q);
            }
            QuestionDao dao = new QuestionDao();
            dao.updateQuestionGroupOrder(groupList);

        }
    }

    /**
     * updates a question with new dependency information.
     *
     * @param questionId
     * @param dep
     */
    @Override
    public void updateQuestionDependency(Long questionId,
            QuestionDependencyDto dep) {
        QuestionDao qDao = new QuestionDao();
        Question q = qDao.getByKey(questionId, false);
        if (q != null) {
            if (dep != null) {
                q.setDependentFlag(true);
                q.setDependentQuestionId(dep.getQuestionId());
                q.setDependentQuestionAnswer(dep.getAnswerValue());
            } else {
                q.setDependentFlag(false);
            }
        }
    }

    /**
     * returns a surveyDto populated from the published xml. This domain graph lacks many keyIds so
     * it is not suitable for updating the survey structure. It is, however, suitable for rendering
     * the survey and collecting responses.
     *
     * @param surveyId
     * @return
     */
    @Override
    public SurveyDto getPublishedSurvey(String surveyId) {
        SurveyDto dto = null;
        try {
            try {
                if (cache != null) {
                    if (cache.containsKey(PUB_CACHE_PREFIX + surveyId)) {
                        dto = (SurveyDto) cache
                                .get(PUB_CACHE_PREFIX + surveyId);
                        if (dto != null) {
                            return dto;
                        }
                    }
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Could not check cache", e);
            }
            URL url = new URL(PropertyUtil.getProperty(SURVEY_S3_PROP)
                    + PropertyUtil.getProperty(SURVEY_DIR_PROP) + "/"
                    + surveyId + ".xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    url.openStream(), "UTF-8"));
            StringBuffer buff = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buff.append(line);
            }
            reader.close();
            String fullContent = buff.toString();
            if (fullContent.trim().length() > 0) {
                SurveyXmlDtoHelper helper = new SurveyXmlDtoHelper();
                dto = helper.parseAsDtoGraph(fullContent.trim(), new Long(
                        surveyId));
                try {
                    if (cache != null) {
                        cache.put(PUB_CACHE_PREFIX + surveyId, dto);
                    }
                } catch (Exception e) {
                    log.log(Level.WARNING, "Could not cache result", e);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not popuate survey from xml", e);
        }
        return dto;
    }

    /**
     * fires an async request to generate a bootstrap xml file
     *
     * @param surveyIdList
     * @param dbInstructions
     * @param notificationEmail
     */
    @Override
    public void generateBootstrapFile(List<Long> surveyIdList,
            String dbInstructions, String notificationEmail) {
        StringBuilder buf = new StringBuilder();
        if (surveyIdList != null) {
            for (int i = 0; i < surveyIdList.size(); i++) {
                if (i > 0) {
                    buf.append(BootstrapGeneratorRequest.DELMITER);
                }
                buf.append(surveyIdList.get(i).toString());
            }
        }
        Queue queue = QueueFactory.getQueue("background-processing");
        queue.add(TaskOptions.Builder
                .withUrl("/app_worker/bootstrapgen")
                .param(BootstrapGeneratorRequest.ACTION_PARAM,
                        BootstrapGeneratorRequest.GEN_ACTION)
                .param(BootstrapGeneratorRequest.SURVEY_ID_LIST_PARAM,
                        buf.toString())
                .param(BootstrapGeneratorRequest.EMAIL_PARAM, notificationEmail)
                .param(BootstrapGeneratorRequest.DB_PARAM,
                        dbInstructions != null ? dbInstructions : ""));
    }

    /**
     * returns a survey (core info only). If you need all data, use loadFullSurvey.
     */
    @Override
    public SurveyDto findSurvey(Long id) {
        SurveyDto dto = null;
        if (id != null) {
            Survey s = surveyDao.getById(id);
            if (s != null) {
                dto = new SurveyDto();
                DtoMarshaller.copyToDto(s, dto);
                dto.setQuestionGroupList(null);
            }
        }
        return dto;
    }

    /**
     * saves a Message indicating that the survey has been updated and needs to be republished. If
     * there is already a message of this type for the surveyId passed in, the last update time
     * stamp of the message is updated instead of creating a duplicate message.
     *
     * @param surveyId
     */
    private void saveSurveyUpdateMessage(Long surveyId) {
        Message m = null;
        List<Message> messages = messageDao.listBySubject(
                SURVEY_UPDATE_MESSAGE_ACTION, surveyId, null);
        Survey s = surveyDao.getByKey(surveyId);
        if (messages != null && messages.size() > 0) {
            m = messages.get(0);
            m.setLastUpdateDateTime(new Date());
        } else {
            m = new Message();
            m.setActionAbout(SURVEY_UPDATE_MESSAGE_ACTION);
            m.setObjectId(surveyId);
            m.setShortMessage(SURVEY_UPDATE_MESSAGE);
        }
        if (s != null) {
            m.setObjectTitle(s.getPath() + "/" + s.getName());

        }
        try {
            UserService userService = UserServiceFactory.getUserService();
            if (userService != null && userService.isUserLoggedIn()) {
                User u = userService.getCurrentUser();
                if (u != null) {
                    m.setUserName(u.getEmail());
                }
            }
        } catch (Exception e) {
            log.log(Level.WARNING,
                    "Could not get current user when publishing message");
        }
        messageDao.save(m);
    }

    /**
     * marks that a set of changes to a survey are done so we can publish a notification
     *
     * @param id
     */
    @Override
    public void markSurveyChangesComplete(Long surveyId) {
        Message m = null;
        Survey s = surveyDao.getByKey(surveyId);
        m = new Message();
        m.setActionAbout(SURVEY_CHANGE_COMPLTE_MESSAGE_ACTION);
        m.setObjectId(surveyId);
        m.setShortMessage(SURVEY_CHANGE_COMPLETE_MESSAGE);
        if (s != null) {
            m.setObjectTitle(s.getPath() + "/" + s.getName());
        }
        try {
            UserService userService = UserServiceFactory.getUserService();
            if (userService != null && userService.isUserLoggedIn()) {
                User u = userService.getCurrentUser();
                if (u != null) {
                    m.setUserName(u.getEmail());
                }
            }
        } catch (Exception e) {
            log.log(Level.WARNING,
                    "Could not get current user when publishing message");
        }
        messageDao.save(m);
    }

    /**
     * lists the base question info for all questions that depend on the questionId passed in
     *
     * @param questionId
     * @return
     */
    @Override
    public ArrayList<QuestionDto> listQuestionsDependentOnQuestion(
            Long questionId) {
        QuestionDao dao = new QuestionDao();
        List<Question> qList = dao.listQuestionsByDependency(questionId);
        ArrayList<QuestionDto> dtoList = null;
        if (qList != null) {
            dtoList = new ArrayList<QuestionDto>();
            for (Question q : qList) {
                QuestionDto dto = new QuestionDto();
                DtoMarshaller.copyToDto(q, dto);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }
}
