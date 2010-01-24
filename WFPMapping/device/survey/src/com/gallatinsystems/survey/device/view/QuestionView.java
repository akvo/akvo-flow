package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gallatinsystems.survey.device.domain.Question;
import com.gallatinsystems.survey.device.event.QuestionInteractionEvent;
import com.gallatinsystems.survey.device.event.QuestionInteractionListener;

/**
 * basic question view. This just displays the question text. It also provides a
 * mechanism to register/notify QuestionInteractionListeners when a
 * QuestionInteractionEvent occurs.
 * 
 * It is unlikely anyone will ever use this class directly (a subclass should be
 * used).
 * 
 * TODO: implement tool-tip pop-up
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionView extends TableLayout {

    protected static final int DEFAULT_WIDTH = 300;
    private TextView questionText;
    protected Question question;
    private List<QuestionInteractionListener> listeners;
    private TextView tipText;
    private ImageButton tipImage;

    /**
     * install a single tableRow containing a textView with the question text
     * 
     * @param context
     * @param q
     */
    public QuestionView(Context context, Question q) {
        super(context);
        question = q;
        TableRow tr = new TableRow(context);
        questionText = new TextView(context);
        questionText.setWidth(DEFAULT_WIDTH);
        questionText.setText(q.getText());
        tr.addView(questionText);
        if (question.getTip() != null) {         
            tipImage = new ImageButton(context);
            tipImage.setImageResource(android.R.drawable.ic_dialog_info);
            tr.addView(tipImage);
            tipImage.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v
                            .getContext());
                    TextView tipText = new TextView(v.getContext());
                    tipText.setText(Html.fromHtml(question.getTip()));
                    builder.setView(tipText);                    
                    builder.show();
                    
                }
            });
        }
        addView(tr);
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
        if (listener != null) {
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
            for (QuestionInteractionListener l : listeners) {
                l.onQuestionInteraction(event);
            }
        }
    }

    /**
     * method that can be overridden by sub classes if they want to have some
     * sort of visual response to a question interaction.
     */
    public void questionComplete() {
        // do nothing
    }

}
