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

package com.gallatinsystems.framework.dataexport.applet;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Modal dialog to display progress of a task being performed by a Swing client (applet or Swing
 * app). The progress is reported in 2 ways:
 * <ul>
 * <li>Overall status - represented with a progress bar and text saying "Step x of N"</li>
 * <li>Current task - textual information about what is currently happening within the currently
 * executing step (i.e. "Fetching question 1 of 16")</li>
 * </ul>
 * 
 * @author Christopher Fagiani
 */
public class ProgressDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -8352578816957180670L;
    private static final String DEFAULT_LOCALE = "en";
    private static final int HEIGHT = 200;
    private static final int WIDTH = 400;
    private JProgressBar progressBar;
    private JLabel overallLabel;
    private JLabel currentStepLabel;
    private JButton closeButton;
    private String locale;
    private int currentStep;
    private int maxSteps;
    private static Map<String, String> STEP;
    private static Map<String, String> OVERALL;
    private static Map<String, String> CURRENT;
    private static Map<String, String> CLOSE;

    static {
        STEP = new HashMap<String, String>();
        STEP.put("en", "Step X of N");
        STEP.put("es", "Paso X de N");

        OVERALL = new HashMap<String, String>();
        OVERALL.put("en", "Overall Progress");
        OVERALL.put("es", "Progreso General");

        CURRENT = new HashMap<String, String>();
        CURRENT.put("en", "Current status");
        CURRENT.put("es", "Estado Actual");

        CLOSE = new HashMap<String, String>();
        CLOSE.put("en", "Close");
        CLOSE.put("es", "Cerrar");
    }

    /**
     * initializes the dialog to use the locale (language) passed in. This box will consider
     * totalSteps as the number of steps until completion (for displaying a progress bar).
     * 
     * @param totalSteps
     * @param locale
     */
    public ProgressDialog(int totalSteps, String locale) {
        super((Frame) null, false);
        setSize(WIDTH, HEIGHT);
        // this puts it in the center of the screen
        setLocationRelativeTo(null);
        this.locale = locale;
        if (this.locale == null) {
            this.locale = DEFAULT_LOCALE;
        }
        progressBar = new JProgressBar(0, totalSteps + 1);
        overallLabel = new JLabel();
        currentStepLabel = new JLabel();
        closeButton = new JButton(CLOSE.get(locale));
        currentStep = -1;
        maxSteps = totalSteps;
        installControls();
    }

    /**
     * installs the UI widgets
     */
    private void installControls() {
        JPanel contents = (JPanel) getContentPane();
        contents.setLayout(new GridLayout(3, 1));
        contents.add(constructPanel(OVERALL.get(locale), overallLabel,
                progressBar));
        contents.add(constructPanel(CURRENT.get(locale), currentStepLabel));
        closeButton.setVisible(false);
        closeButton.addActionListener(this);
        contents.add(closeButton);
        update(currentStep, null);
    }

    /**
     * constructs a simple panel that uses a titled/lined compound border and adds all the items
     * passed in to the panel using the default layout manager.
     * 
     * @param title
     * @param content
     * @return
     */
    private JPanel constructPanel(String title, JComponent... items) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), title));
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                panel.add(items[i]);
            }
        }

        return panel;
    }

    /**
     * updates the current status displayed by this dialog.
     * 
     * @param step
     * @param task
     */
    public void update(int step, String task) {
        update(step, task, false);
    }

    /**
     * updates the current status displayed by this dialog.
     * 
     * @param step
     * @param task
     */
    public void update(int step, String task, boolean isComplete) {
        if (task == null) {
            currentStepLabel.setText("");
        } else {
            currentStepLabel.setText(task);
        }
        if (step != currentStep) {
            currentStep = step;
            progressBar.setValue(currentStep);
            if (currentStep >= maxSteps || isComplete) {
                String statusText = STEP.get(locale);
                statusText = statusText.replaceAll("X", maxSteps + "");
                overallLabel.setText(statusText.replaceAll("N", maxSteps + ""));
                progressBar.setValue(progressBar.getMaximum());
                closeButton.setVisible(true);
            } else {
                String statusText = STEP.get(locale);
                statusText = statusText.replaceAll("X", currentStep + "");
                overallLabel.setText(statusText.replaceAll("N", maxSteps + ""));
            }
        }
    }

    /**
     * button handler for the closeButton
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            this.setVisible(false);
        }
    }

}
