package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.AltText;
import com.gallatinsystems.survey.device.domain.Dependency;
import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.event.QuestionInteractionEvent;
import com.gallatinsystems.survey.device.event.QuestionInteractionListener;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * basic question view. This just displays the question text. It also provides a
 * mechanism to register/notify QuestionInteractionListeners when a
 * QuestionInteractionEvent occurs.
 * 
 * It is unlikely anyone will ever use this class directly (a subclass should be
 * used).
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionView extends TableLayout implements
		QuestionInteractionListener {

	private static final String VIDEO_HELP = "video";
	private static final String TEXT_HELP = "txt";
	private static final String PHOTO_HELP = "photo";

	protected static final int DEFAULT_WIDTH = 290;
	private TextView questionText;

	protected Question question;
	private QuestionResponse response;
	private ArrayList<QuestionInteractionListener> listeners;
	private ImageButton tipImage;
	protected String[] langs = null;
	protected static String[] colors = null;

	/**
	 * install a single tableRow containing a textView with the question text
	 * 
	 * @param context
	 * @param q
	 */
	public QuestionView(Context context, Question q, String[] langs) {
		super(context);
		question = q;
		this.langs = langs;
		if (colors == null) {
			colors = context.getResources().getStringArray(R.array.colors);
		}
		TableRow tr = new TableRow(context);
		questionText = new TextView(context);
		questionText.setWidth(DEFAULT_WIDTH);

		questionText.setText(formText(), BufferType.SPANNABLE);
		tr.addView(questionText);

		// if there is a tip for this question, construct an alert dialog box
		// with the data
		final int tips = question.getTipCount();
		if (tips > 0) {
			tipImage = new ImageButton(context);
			tipImage.setImageResource(android.R.drawable.ic_dialog_info);
			tr.addView(tipImage);
			tipImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tips > 1) {
						displayHelpChoices();
					} else {
						if (question.getTip() != null) {
							displayHelp(TEXT_HELP);
						} else if (question.getVideo() != null) {
							displayHelp(VIDEO_HELP);
						} else {
							displayHelp(PHOTO_HELP);
						}
					}
				}
			});
		}
		addView(tr);
		// if this question has 1 or more dependencies, then it needs to be
		// invisible initially
		if (question.getDependencies() != null
				&& question.getDependencies().size() > 0) {
			setVisibility(View.GONE);
		}
	}

	/**
	 * forms the question text based on the selected languages
	 * 
	 * @return
	 */
	private Spanned formText() {
		boolean isFirst = true;
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < langs.length; i++) {
			if (ConstantUtil.ENGLISH_CODE.equalsIgnoreCase(langs[i])) {
				if (!isFirst) {
					text.append(" / ");
				} else {
					isFirst = false;
				}
				text.append(question.getText());
			} else {
				AltText txt = question.getAltText(langs[i]);
				if (txt != null) {
					if (!isFirst) {
						text.append(" / ");
					} else {
						isFirst = false;
					}
					text.append("<font color='").append(colors[i]).append("'>")
							.append(txt.getText()).append("</font>");
				}
			}
		}
		if (question.isMandatory()) {
			text = text.append("*");
		}
		return Html.fromHtml(text.toString());
	}

	/**
	 * updates the question's visible languages
	 * 
	 * @param languageCodes
	 */
	public void updateSelectedLanguages(String[] languageCodes) {
		langs = languageCodes;
		questionText.setText(formText());
	}

	/**
	 * displays a dialog box with options for each of the help types that have
	 * been initialized for this particular question.
	 */
	private void displayHelpChoices() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.helpheading);
		final CharSequence[] items = new CharSequence[question.getTipCount()];
		final Resources resources = getResources();
		int itemIndex = 0;

		if (question.getImages() != null && question.getImages().size() > 0) {
			items[itemIndex++] = resources.getString(R.string.photohelpoption);
		}
		if (question.getVideo() != null) {
			items[itemIndex++] = resources.getString(R.string.videohelpoption);
		}
		if (question.getTip() != null) {
			items[itemIndex++] = resources.getString(R.string.texthelpoption);
		}
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String val = items[id].toString();
				if (resources.getString(R.string.texthelpoption).equals(val)) {
					displayHelp(TEXT_HELP);
				} else if (resources.getString(R.string.videohelpoption)
						.equals(val)) {
					displayHelp(VIDEO_HELP);
				} else {
					displayHelp(PHOTO_HELP);
				}
				dialog.dismiss();
			}
		});
		builder.show();
	}

	/**
	 * displays the selected help type
	 * 
	 * @param type
	 */
	private void displayHelp(String type) {
		if (VIDEO_HELP.equals(type)) {
			notifyQuestionListeners(QuestionInteractionEvent.VIDEO_TIP_VIEW);
		} else if (TEXT_HELP.equals(type)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			TextView tipText = new TextView(getContext());
			tipText.setText(Html.fromHtml(question.getTip()));
			builder.setView(tipText);
			builder.setPositiveButton(R.string.okbutton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.show();
		} else {
			notifyQuestionListeners(QuestionInteractionEvent.PHOTO_TIP_VIEW);
		}
	}

	/**
	 * adds a listener to the internal list of clients to be notified on an
	 * event
	 * 
	 * @param listener
	 */
	public void addQuestionInteractionListener(
			QuestionInteractionListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<QuestionInteractionListener>();
		}
		if (listener != null && !listeners.contains(listener)
				&& listener != this) {
			listeners.add(listener);
		}
	}

	/**
	 * notifies each QuestionInteractionListener registered with this question.
	 * This is done serially on the calling thread.
	 * 
	 * @param type
	 */
	protected void notifyQuestionListeners(String type) {
		if (listeners != null) {
			QuestionInteractionEvent event = new QuestionInteractionEvent(type,
					this);
			for (int i = 0; i < listeners.size(); i++) {
				listeners.get(i).onQuestionInteraction(event);
			}
		}
	}

	/**
	 * method that can be overridden by sub classes if they want to have some
	 * sort of visual response to a question interaction.
	 */
	public void questionComplete(Bundle data) {
		// do nothing
	}

	/**
	 * method that should be overridden by sub classes to clear current value
	 * 
	 */
	public void resetQuestion() {
		setResponse(null, false);
	}

	public void onQuestionInteraction(QuestionInteractionEvent event) {

		if (QuestionInteractionEvent.QUESTION_ANSWER_EVENT.equals(event
				.getEventType())) {
			// if this question is dependent, see if it has been satisfied
			ArrayList<Dependency> dependencies = question.getDependencies();
			if (dependencies != null) {
				for (int i = 0; i < dependencies.size(); i++) {
					Dependency d = dependencies.get(i);
					if (d.getQuestion().equalsIgnoreCase(
							event.getSource().getQuestion().getId())) {
						if (handleDependencyParentResponse(d, event.getSource()
								.getResponse(true))) {
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * updates the state of this question view based on the value in the
	 * dependency parent response. This method returns true if there is a value
	 * match and false otherwise.
	 * 
	 * @param dep
	 * @param resp
	 * @return
	 */
	public boolean handleDependencyParentResponse(Dependency dep,
			QuestionResponse resp) {
		// if we're here, then the question on which we depend
		// has been answered. Check the value to see if it's the
		// one we are looking for
		if (dep.getAnswer() != null && resp != null
				&& dep.getAnswer().equalsIgnoreCase(resp.getValue())) {
			setVisibility(View.VISIBLE);
			return true;
		} else {
			setVisibility(View.GONE);
		}
		return false;
	}

	/**
	 * this method should be overridden by subclasses so they can record input
	 * in a QuestionResponse object
	 */
	public void captureResponse() {
		// NO OP
	}

	/**
	 * this method should be overridden by subclasses so they can record input
	 * in a QuestionResponse object
	 */
	public void captureResponse(boolean suppressListeners) {
		// NO OP
	}

	/**
	 * this method should be overridden by subclasses so they can manage the UI
	 * changes when resetting the value
	 * 
	 * @param resp
	 */
	public void rehydrate(QuestionResponse resp) {
		setResponse(resp, true);
	}

	public QuestionResponse getResponse(boolean suppressListeners) {
		if (response == null
				|| (ConstantUtil.VALUE_RESPONSE_TYPE.equals(response.getType()) && (response
						.getValue() == null || response.getValue().trim()
						.length() == 0))) {
			captureResponse(suppressListeners);
		}
		return response;
	}

	public QuestionResponse getResponse() {

		return getResponse(false);
	}

	public void setResponse(QuestionResponse response) {
		setResponse(response, false);
	}

	public void setResponse(QuestionResponse response, boolean suppressListeners) {
		if (response != null) {
			if (this.response == null) {
				this.response = response;
			} else {
				// we need to preserve the ID so we don't get duplicates in the
				// db
				this.response.setType(response.getType());
				this.response.setValue(response.getValue());
			}
		} else {
			this.response = response;
		}
		if (!suppressListeners) {
			notifyQuestionListeners(QuestionInteractionEvent.QUESTION_ANSWER_EVENT);
		}
	}

	public Question getQuestion() {
		return question;
	}

	public void setTextSize(float size) {
		questionText.setTextSize(size);
	}
}
