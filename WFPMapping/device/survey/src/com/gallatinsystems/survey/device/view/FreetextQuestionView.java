package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.widget.EditText;
import android.widget.TableRow;

import com.gallatinsystems.survey.device.domain.Question;

/**
 * Question that supports free-text input via the keyboard
 * 
 * TODO: add validation rules
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class FreetextQuestionView extends QuestionView {
    private EditText freetextEdit;

    public FreetextQuestionView(Context context, Question q) {
        super(context, q);
        init();
    }

    protected void init() {
        Context context = getContext();
        TableRow tr = new TableRow(context);
        freetextEdit = new EditText(context);
        freetextEdit.setWidth(DEFAULT_WIDTH);
        tr.addView(freetextEdit);        
        addView(tr);
    }
}
