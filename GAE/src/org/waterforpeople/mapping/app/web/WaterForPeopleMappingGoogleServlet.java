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

package org.waterforpeople.mapping.app.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;
import org.waterforpeople.mapping.dao.KMLDAO;

import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.gis.map.dao.MapFragmentDao;
import com.gallatinsystems.gis.map.domain.MapFragment;
import com.gallatinsystems.gis.map.domain.MapFragment.FRAGMENTTYPE;
import com.google.appengine.api.datastore.Blob;

@SuppressWarnings("serial")
public class WaterForPeopleMappingGoogleServlet extends HttpServlet {
    @SuppressWarnings("unused")
    private static final Logger log = Logger
            .getLogger(WaterForPeopleMappingGoogleServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String showKML = req.getParameter("showKML");
        @SuppressWarnings("unused")
        String processFile = req.getParameter("processFile");
        String showRegion = req.getParameter("showRegion");
        String action = req.getParameter("action");
        String countryCode = req.getParameter("countryCode");
        if (showKML != null) {
            Long kmlKey = null;
            if (req.getParameter("kmlID") != null) {
                kmlKey = Long.parseLong(req.getParameter("kmlID"));
            }
            if (kmlKey != null) {
                KMLDAO kmlDAO = new KMLDAO();
                String kmlString = kmlDAO.getKML(kmlKey);
                resp.setContentType("application/vnd.google-earth.kml+xml");
                resp.getWriter().println(kmlString);

            } else {
                KMLGenerator kmlGen = new KMLGenerator();
                String placemarksDocument = null;
                String timestamp = DateFormatUtils.formatUTC(new Date(),
                        DateFormatUtils.ISO_DATE_FORMAT
                                .getPattern());
                if (countryCode != null) {
                    placemarksDocument = kmlGen.generateDocument(
                            "PlacemarksNewLook.vm", countryCode);
                    resp.setHeader("Content-Disposition",
                            "inline; filename=waterforpeoplemapping_" + countryCode + "_"
                                    + timestamp + ".kmz;");
                } else {
                    placemarksDocument = kmlGen
                            .generateDocument("PlacemarksNewLook.vm");
                    resp.setHeader("Content-Disposition",
                            "inline; filename=waterforpeoplemapping_" + timestamp + "_.kmz;");
                }
                // ToDo implement kmz compression now that kmls are so big
                // application/vnd.google-earth.kmz
                resp.setContentType("application/vnd.google-earth.kmz+xml");
                ServletOutputStream out = resp.getOutputStream();

                ByteArrayOutputStream os = ZipUtil
                        .generateZip(placemarksDocument, "waterforpeoplemapping.kml");
                out.write(os.toByteArray());
                out.flush();

            }
        } else if (showRegion != null) {
            KMLGenerator kmlGen = new KMLGenerator();
            String placemarksDocument = kmlGen
                    .generateRegionDocumentString("Regions.vm");
            resp.setContentType("application/vnd.google-earth.kml+xml");
            resp.getWriter().println(placemarksDocument);

        } else if ("getLatestMap".equals(action)) {
            MapFragmentDao mfDao = new MapFragmentDao();
            List<MapFragment> mfList = mfDao.searchMapFragments(null, null,
                    null, FRAGMENTTYPE.GLOBAL_ALL_PLACEMARKS, "all",
                    "createdDateTime", "desc");
            Blob map = mfList.get(0).getBlob();
            resp.setContentType("application/vnd.google-earth.kmz+xml");
            ServletOutputStream out = resp.getOutputStream();
            resp.setHeader("Content-Disposition",
                    "inline; filename=waterforpeoplemapping.kmz;");
            out.write(map.getBytes());
            out.flush();
        }
    }

}
