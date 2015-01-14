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

package com.gallatinsystems.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * wrapper class to facilitate generation of zip files
 */
public class ZipUtil {

    /**
     * zips the contents of the string passed in into a file called "waterforpeoplemapping.kml"
     * 
     * @param kmlContents
     * @return
     * @deprecated
     */
    public static ByteArrayOutputStream generateZip(String kmlContents) {
        return generateZip(kmlContents, "waterforpeoplemapping.kml");
    }

    /**
     * generates a zip file containg the content of the content string into a file named filename.
     * 
     * @param content
     * @param filename
     * @return ByteArrayOutputStream of the zip encoded file
     */
    public static ByteArrayOutputStream generateZip(String content,
            String filename) {
        ZipOutputStream zipOut = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            zipOut = new ZipOutputStream(bos);
            zipOut.setLevel(ZipOutputStream.DEFLATED);
            ZipEntry entry = new ZipEntry(filename);
            zipOut.putNextEntry(entry);
            zipOut.write(content.getBytes("UTF-8"));
            zipOut.closeEntry();
            zipOut.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos;

    }

    /**
     * generates a zip containing multiple entries
     * 
     * @param contents - map of data to zip. Keys are filenames for the entry and values is the
     *            content that will be written as an UTF-8 string
     * @return
     */
    public static ByteArrayOutputStream generateZip(Map<String, String> contents, Map<String, byte[]> resources) {
        ZipOutputStream zipOut = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            zipOut = new ZipOutputStream(bos);
            zipOut.setLevel(ZipOutputStream.DEFLATED);
            for (Entry<String, String> contentEntry : contents.entrySet()) {
                ZipEntry entry = new ZipEntry(contentEntry.getKey());
                zipOut.putNextEntry(entry);
                zipOut.write(contentEntry.getValue().getBytes("UTF-8"));
                zipOut.closeEntry();
            }
            for (Entry<String, byte[]> resource : resources.entrySet()) {
                ZipEntry entry = new ZipEntry(resource.getKey());
                zipOut.putNextEntry(entry);
                zipOut.write(resource.getValue());
                zipOut.closeEntry();
            }
            zipOut.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos;
    }

    /**
     * decodes the byte array representing the zip file into a string. This assumes the zip contains
     * only 1 file.
     * 
     * @param contents
     * @return
     * @throws IOException
     */
    public static String unZip(byte[] contents) throws IOException {
        return unZip(contents, null);
    }

    /**
     * unzips a single zip entry (file within a zip) and returns the content as a string.
     * 
     * @param contents
     * @param entryName
     * @return
     * @throws IOException
     */
    public static String unZip(byte[] contents, String entryName)
            throws IOException {
        ByteArrayInputStream zipContents = new ByteArrayInputStream(contents);
        ZipInputStream zis = new ZipInputStream(zipContents);
        ZipEntry entry;
        StringBuilder line = new StringBuilder();
        while ((entry = zis.getNextEntry()) != null) {
            if (entryName == null
                    || entryName.equalsIgnoreCase(entry.getName())) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];
                int size;
                while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, size);
                }
                line.append(out.toString());

                out.close();
            }
        }
        zis.closeEntry();

        return line.toString();
    }

}
