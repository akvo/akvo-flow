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

package org.waterforpeople.mapping.dataexport;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDto;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDtoResponse;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

public class KMLApplet extends JApplet implements Runnable {

    private static final long serialVersionUID = -450706177231338054L;
    private JLabel statusLabel;

    private String serverBase;
    private VelocityEngine engine;

    @Override
    public void run() {

        try {
            SwingUtilities.invokeLater(new StatusUpdater("Prompting for File"));
            String filePath = promptForFile();
            if (filePath != null) {
                System.out.println(filePath);
                SwingUtilities.invokeLater(new StatusUpdater("Running export"));
                executeExport(filePath);
                SwingUtilities
                        .invokeLater(new StatusUpdater("Export Complete"));
            } else {
                SwingUtilities.invokeLater(new StatusUpdater("Cancelled"));
            }
        } catch (Exception e) {
            SwingUtilities
                    .invokeLater(new StatusUpdater("Backout Failed: " + e));
        }
    }

    ClassLoader cl = null;

    public void init() {
        cl = this.getClass().getClassLoader();
        engine = new VelocityEngine();
        engine.setProperty("runtime.log.logsystem.class",
                "org.apache.velocity.runtime.log.NullLogChute");
        try {
            engine.init();
        } catch (Exception e) {
            System.out.println("Could not initialize velocity" + e);
        }
        statusLabel = new JLabel();
        getContentPane().add(statusLabel);
        if (getParameter("serverOverride") != null
                && getParameter("serverOverride").trim().length() > 0) {
            serverBase = getParameter("serverOverride").trim();
        } else {
            serverBase = getCodeBase().toString();
        }
        if (serverBase.trim().endsWith("/")) {
            serverBase = serverBase.trim().substring(0,
                    serverBase.lastIndexOf("/"));
        }
        System.out.println("ServerBase: " + serverBase);

        Thread worker = new Thread(this);
        worker.start();
    }

    private void executeExport(String path) {
        try {
            System.out.println("File to save to: " + path);
            ArrayList<String> countryList = new ArrayList<String>();
            countryList.add("BF");
            countryList.add("MW");
            countryList.add("NL");
            countryList.add("RW");
            countryList.add("BO");
            countryList.add("PE");
            countryList.add("GT");
            countryList.add("IN");
            countryList.add("NI");
            countryList.add("SV");
            countryList.add("LR");
            countryList.add("HT");
            countryList.add("ID");
            countryList.add("SD");
            countryList.add("NG");
            countryList.add("NP");
            countryList.add("EC");
            countryList.add("GN");
            countryList.add("CI");
            countryList.add("CM");
            countryList.add("NG");
            countryList.add("SL");
            countryList.add("DO");
            countryList.add("GH");
            countryList.add("UG");
            processFile(path, countryList);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void processFile(String fileName, ArrayList<String> countryList)
            throws Exception {
        System.out.println("Calling GenerateDocument");
        VelocityContext context = new VelocityContext();
        File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        ZipOutputStream zipOut = null;
        try {

            zipOut = new ZipOutputStream(new FileOutputStream(fileName));
            zipOut.setLevel(ZipOutputStream.DEFLATED);
            ZipEntry entry = new ZipEntry("ap.kml");
            zipOut.putNextEntry(entry);

            zipOut.write(mergeContext(context, "template/DocumentHead.vm")
                    .getBytes("UTF-8"));
            for (String countryCode : countryList) {
                int i = 0;
                String cursor = null;
                PlacemarkDtoResponse pdr = BulkDataServiceClient
                        .fetchPlacemarks(countryCode, serverBase, cursor);
                if (pdr != null) {
                    cursor = pdr.getCursor();
                    List<PlacemarkDto> placemarkDtoList = pdr.getDtoList();
                    SwingUtilities.invokeLater(new StatusUpdater(
                            "Staring to processes " + countryCode));
                    writePlacemark(placemarkDtoList, zipOut);
                    SwingUtilities.invokeLater(new StatusUpdater(
                            "Processing complete for " + countryCode));
                    while (cursor != null) {
                        pdr = BulkDataServiceClient.fetchPlacemarks(
                                countryCode, serverBase, cursor);
                        if (pdr != null) {
                            if (pdr.getCursor() != null)
                                cursor = pdr.getCursor();
                            else
                                cursor = null;
                            placemarkDtoList = pdr.getDtoList();
                            System.out.println("Starting to process: "
                                    + countryCode);
                            writePlacemark(placemarkDtoList, zipOut);
                            System.out
                                    .println("Fetching next set of records for: "
                                            + countryCode + " : " + i++);
                        } else {
                            break;
                        }
                    }
                }
            }
            zipOut.write(mergeContext(context, "template/DocumentFooter.vm")
                    .getBytes("UTF-8"));
            zipOut.closeEntry();
            zipOut.close();
        } catch (Exception ex) {
            System.out.println(ex + " " + ex.getMessage() + " ");
            ex.printStackTrace(System.out);
        }
    }

    private void writePlacemark(List<PlacemarkDto> placemarkDtoList,
            ZipOutputStream zipOut) throws Exception {
        if (placemarkDtoList != null) {
            for (PlacemarkDto pm : placemarkDtoList) {
                if (pm != null) {
                    if (pm.getCollectionDate() != null
                            && pm.getLatitude() != null
                            && pm.getLatitude() != 0
                            && pm.getLongitude() != null
                            && pm.getLongitude() != 0) {
                        VelocityContext vc = new VelocityContext();
                        String timestamp = DateFormatUtils.formatUTC(
                                pm.getCollectionDate(),
                                DateFormatUtils.ISO_DATE_FORMAT.getPattern());
                        vc.put("timestamp", timestamp);
                        vc.put("pinStyle", pm.getPinStyle());
                        vc.put("balloon", pm.getPlacemarkContents());
                        vc.put("longitude", pm.getLongitude());
                        vc.put("latitude", pm.getLatitude());
                        vc.put("altitude", pm.getAltitude());
                        vc.put("communityCode", pm.getCommunityCode());
                        vc.put("communityName", pm.getCommunityCode());
                        String placemark = mergeContext(vc,
                                "template/PlacemarksNewLook.vm");
                        zipOut.write(placemark.getBytes("UTF-8"));
                    }
                }
            }
        }
    }

    private String promptForFile() {
        final JFileChooser fc = new JFileChooser();
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String fileName = "GoogleEarth-" + df.format(new Date()) + ".kmz";
        fc.setSelectedFile(new File(fileName));

        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * merges a hydrated context with a template identified by the templateName passed in.
     * 
     * @param context
     * @param templateName
     * @return
     * @throws Exception
     */
    private String mergeContext(VelocityContext context, String templateName)
            throws Exception {
        String templateContents = loadResourceAsString(templateName);
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, "mystring", templateContents);
        return writer.toString();
    }

    private String loadResourceAsString(String resourceName) throws Exception {
        InputStream in = cl.getResourceAsStream(resourceName);
        String resourceContents = readInputStreamAsString(in);
        return resourceContents;
    }

    public static String readInputStreamAsString(InputStream in)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    /**
     * Private class to handle updating of the UI thread from our worker thread
     */
    private class StatusUpdater implements Runnable {

        private String status;

        public StatusUpdater(String val) {
            status = val;
        }

        public void run() {
            statusLabel.setText(status);
        }
    }
}
