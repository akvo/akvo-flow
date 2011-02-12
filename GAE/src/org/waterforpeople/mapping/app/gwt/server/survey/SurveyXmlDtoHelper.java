package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto.Type;

import com.gallatinsystems.survey.domain.xml.AltText;
import com.gallatinsystems.survey.domain.xml.Help;
import com.gallatinsystems.survey.domain.xml.Option;
import com.gallatinsystems.survey.domain.xml.Survey;
import com.gallatinsystems.survey.domain.xml.Text;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;

/**
 * utility class to create partially populated dtos from the survey XML
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyXmlDtoHelper {

	public static final String FREE_QUESTION_TYPE = "free";
	public static final String OPTION_QUESTION_TYPE = "option";
	public static final String GEO_QUESTION_TYPE = "geo";
	public static final String VIDEO_QUESTION_TYPE = "video";
	public static final String PHOTO_QUESTION_TYPE = "photo";
	public static final String SCAN_QUESTION_TYPE = "scan";
	public static final String STRENGTH_QUESTION_TYPE = "strength";

	/**
	 * parses the xml and then converts the xml objects to DTOs
	 * 
	 * @param content
	 * @param surveyId
	 * @return
	 * @throws Exception
	 */
	public SurveyDto parseAsDtoGraph(String content, Long surveyId)
			throws Exception {
		SurveyXMLAdapter xmlAdapter = new SurveyXMLAdapter();
		Survey survey = xmlAdapter.unmarshall(content);
		SurveyDto dto = null;
		if (survey != null) {
			dto = new SurveyDto();
			dto.setKeyId(surveyId);

			List<QuestionGroupDto> groupList = new ArrayList<QuestionGroupDto>();
			int count = 0;
			for (com.gallatinsystems.survey.domain.xml.QuestionGroup qg : survey
					.getQuestionGroup()) {
				QuestionGroupDto group = new QuestionGroupDto();
				group.setSurveyId(dto.getKeyId());
				group.setName(qg.getHeading() != null ? qg.getHeading()
						.getContent() : "");
				group.setCode(qg.getHeading() != null ? qg.getHeading()
						.getContent() : "");
				group.setOrder(qg.getOrder() != null ? Integer.parseInt(qg
						.getOrder()) : count);

				for (com.gallatinsystems.survey.domain.xml.Question q : qg
						.getQuestion()) {
					QuestionDto qDto = new QuestionDto();
					qDto.setText(q.getText().getContent());
					qDto.setKeyId(new Long(q.getId()));
					qDto.setMandatoryFlag(new Boolean(q.getMandatory()));
					qDto.setOrder(new Integer(q.getOrder()));
					String type = q.getType();
					if (FREE_QUESTION_TYPE.equals(type)) {
						qDto.setType(QuestionType.FREE_TEXT);
					} else if (OPTION_QUESTION_TYPE.equals(type)) {
						qDto.setType(QuestionType.OPTION);
						OptionContainerDto optC = new OptionContainerDto();
						if (q.getOptions() != null) {
							optC.setAllowMultipleFlag(new Boolean(q
									.getOptions().getAllowMultiple()));
							optC.setAllowOtherFlag(new Boolean(q.getOptions()
									.getAllowOther()));
							for (Option option : q.getOptions().getOption()) {
								QuestionOptionDto opt = new QuestionOptionDto();
								List<Object> contentList = option.getContent();

								for (Object o : contentList) {
									if (o instanceof Text) {
										opt.setText(((Text) o).getContent());
									} else if (o instanceof String) {										
										if (opt.getText() == null
												|| opt.getText().trim()
														.length() == 0) {
											opt.setText((String) o);
										}
									} else if (o instanceof AltText) {
										opt
												.addTranslation(parseTranslation((AltText) o));
									}
								}
								optC.addQuestionOption(opt);
							}
						}
						qDto.setOptionContainerDto(optC);
					} else if (GEO_QUESTION_TYPE.equals(type)) {
						qDto.setType(QuestionType.GEO);
					} else if (VIDEO_QUESTION_TYPE.equals(type)) {
						qDto.setType(QuestionType.VIDEO);
					} else if (PHOTO_QUESTION_TYPE.equals(type)) {
						qDto.setType(QuestionType.PHOTO);
					} else if (SCAN_QUESTION_TYPE.equals(type)) {
						qDto.setType(QuestionType.SCAN);
					} else if (STRENGTH_QUESTION_TYPE.equals(type)) {
						qDto.setType(QuestionType.STRENGTH);
					}
					for (AltText alt : q.getAltText()) {
						qDto.addTranslation(parseTranslation(alt));
					}
					if (q.getHelp() != null) {
						for (Help h : q.getHelp()) {
							QuestionHelpDto hDto = new QuestionHelpDto();
							if ("tip".equalsIgnoreCase(h.getType())) {
								hDto.setType(Type.TEXT);
							} else {
								hDto.setType(Type.valueOf(h.getType()
										.toUpperCase()));
							}
							hDto.setResourceUrl(h.getValue());
							hDto.setText(hDto.getText());
							if (h.getAltText() != null) {
								for (AltText alt : h.getAltText()) {
									hDto.addTranslation(parseTranslation(alt));
								}
							}
							qDto.addQuestionHelp(hDto);
						}
					}
					group.addQuestion(qDto, qDto.getOrder());
				}

				groupList.add(group);
				count++;
			}
			dto.setQuestionGroupList(groupList);
		}
		return dto;
	}

	private TranslationDto parseTranslation(AltText alt) {
		TranslationDto t = new TranslationDto();
		t.setLangCode(alt.getLanguage());
		t.setText(alt.getContent());
		return t;
	}
}
