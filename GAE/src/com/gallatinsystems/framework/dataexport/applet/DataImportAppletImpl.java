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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * applet wrapper for data import utilities
 * 
 * @author Christopher Fagiani
 */
public class DataImportAppletImpl extends AbstractDataImportExportApplet {

    private static final long serialVersionUID = -545153291195490725L;
    private static final String IMPORT_TYPE_PARAM = "importType";
    private static final String FILE_SELECTION_MODE_PARAM = "selectionMode";
    private static final String DIR_MODE = "dir";
    private DataImportExportFactory dataImporterFactory;
    private JLabel statusLabel;

    /**
     * initializes the applet by reading in the configuration parameters configured in the Applet
     * tag. This applet expects to be passed the following:
     * <ul>
     * <li>factoryClass - fully qualified class name of a DataImportExportFactory instance</li>
     * <li>importType - the type of importer to run. This value will be passed to the importer
     * factory</li>
     * <li>serverOverride (optional) - server base to use for remote api calls. This is only used
     * for testing or for calling a server other than the one hosting the html page that loaded the
     * applet</li>
     * </ul>
     */
    public void init() {
        statusLabel = new JLabel();
        getContentPane().add(statusLabel);
        String type = getParameter(IMPORT_TYPE_PARAM);
        String mode = getParameter(FILE_SELECTION_MODE_PARAM);
        dataImporterFactory = getDataImportExportFactory();
        doImport(type, getServerBase(), getConfigCriteria(), mode);
    }

    /**
     * executes the import. This will launch a fileChooser dialog box and allow the user to specify
     * a file for import. Once a file has been chosen, the file will be passed to the dataImporter
     * returned by the factory and the executeImport method will be called on the DataImporter
     * instance.
     * 
     * @param type
     * @param serverBase
     */
    public void doImport(String type, String serverBase,
            Map<String, String> config, String mode) {
        JFileChooser chooser = new JFileChooser();
        if (mode != null && DIR_MODE.equalsIgnoreCase(mode)) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        chooser.showOpenDialog(this);
        if (chooser.getSelectedFile() != null) {
            DataImporter importer = dataImporterFactory.getImporter(type);
            statusLabel.setText("Validating...");
            Map<Integer, String> errorMap = importer.validate(chooser
                    .getSelectedFile());
            if (errorMap.size() == 0) {
                if (serverBase.trim().endsWith("/")) {
                    serverBase = serverBase.trim().substring(0,
                            serverBase.lastIndexOf("/"));
                }
                importer.executeImport(chooser.getSelectedFile(), serverBase,
                        config);
                statusLabel.setText("Import Complete");
            } else {
                statusLabel.setText("Vailidation Failed");
                StringBuilder builder = new StringBuilder();
                builder.append("The survey has the following errors:\n");
                for (Entry<Integer, String> entry : errorMap.entrySet()) {
                    builder.append("Row ").append(entry.getKey()).append(": ")
                            .append(entry.getValue()).append("\n\n");
                }
                final JDialog dia = new JDialog();
                dia.setTitle("Validation Failure");
                final JTextPane text = new JTextPane();
                final JScrollPane scroller = new JScrollPane(text);
                text.setEditable(false);
                text.setText(builder.toString());
                dia.getContentPane().setLayout(new BorderLayout());
                dia.getContentPane().add(scroller, BorderLayout.CENTER);
                dia.setSize(400, 400);
                JButton okButton = new JButton("Ok");
                okButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dia.setVisible(false);
                        text.setText("");
                    }
                });
                dia.getContentPane().add(okButton, BorderLayout.SOUTH);
                dia.setVisible(true);
            }

        }

    }
}
