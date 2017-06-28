/*
 *  Copyright (C) 2013-2017 Stichting Akvo (Akvo Foundation)
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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akvo.flow.locale.UIStrings;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.app.web.rest.security.AppRole;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

public class EnvServlet extends HttpServlet {

    private static final long serialVersionUID = 7830536065252808839L;
    private static final Logger log = Logger.getLogger(EnvServlet.class
            .getName());

    public static final String SHOW_MAPS_PROPERTY_KEY = "showMapsTab";

    private static final ArrayList<String> properties = new ArrayList<String>();

    static {
        properties.add("photo_url_root");
        properties.add("imageroot");
        properties.add("flowServices");
        properties.add("surveyuploadurl");
        properties.add("showStatisticsFeature");
        properties.add("showMonitoringFeature");
        properties.add("mandatoryQuestionID");
        properties.add("showExternalSourcesFeature");
        properties.add("appId");
        properties.add("mapsProvider");
        properties.add(SHOW_MAPS_PROPERTY_KEY);
        properties.add("googleMapsRegionBias");
        properties.add("cartodbHost");
        properties.add("hereMapsAppId");
        properties.add("hereMapsAppCode");
        properties.add("enableDataApproval");
        properties.add("extraMapboxTileLayerMapId");
        properties.add("extraMapboxTileLayerAccessToken");
        properties.add("extraMapboxTileLayerLabel");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty("runtime.log.logsystem.class",
                "org.apache.velocity.runtime.log.NullLogChute");
        try {
            engine.init();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not initialize velocity", e);
        }

        Template t = null;
        try {
            t = engine.getTemplate("Env.vm");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not get the template `CurrentUser`", e);
            return;
        }

        final VelocityContext context = new VelocityContext();
        final Map<String, String> props = PropertyUtil
                .getPropertiesMap(properties);

        // if the showStatisticsFeature is not present in appengine-web.xml, we want it to be false.
        if (props.get("showStatisticsFeature") == null) {
            props.put("showStatisticsFeature", "false");
        }

        if (props.get("showMonitoringFeature") == null) {
            props.put("showMonitoringFeature", "false");
        }

        if (props.get("mandatoryQuestionID") == null) {
            props.put("mandatoryQuestionID", "false");
        }

        if (props.get("showExternalSourcesFeature") == null) {
            props.put("showExternalSourcesFeature", "false");
        }

        if (props.get("enableDataApproval") == null) {
            props.put("enableDataApproval", "false");
        }

        if (props.get("googleMapsRegionBias") == null) {
            props.put("googleMapsRegionBias", "");
        }

        if (props.get("extraMapboxTileLayerMapId") == null) {
            props.put("extraMapboxTileLayerMapId", "");
        }

        if (props.get("extraMapboxTileLayerAccessToken") == null) {
            props.put("extraMapboxTileLayerAccessToken", "");
        }

        if (props.get("extraMapboxTileLayerLabel") == null) {
            props.put("extraMapboxTileLayerLabel", "");
        }

        props.put("appId", SystemProperty.applicationId.get());

        if (!"false".equalsIgnoreCase(props.get(SHOW_MAPS_PROPERTY_KEY))) {
            props.put(SHOW_MAPS_PROPERTY_KEY, "true");
        }

        // load language configuration and strings if present
        addLocale(props);

        final InputStream uiStringsFileStream = this.getClass().getResourceAsStream(
                "/locale/ui-strings.properties");
        InputStream localeStringsFileStream = null;

        if (props.get("locale") != null && !"en".equalsIgnoreCase(props.get("locale"))) {
            localeStringsFileStream = this.getClass().getResourceAsStream(
                    "/locale/" + props.get("locale") + ".properties");
        }
        context.put("localeStrings",
                UIStrings.getStrings(uiStringsFileStream, localeStringsFileStream));

        context.put("env", props);

        final List<Map<String, String>> roles = new ArrayList<Map<String, String>>();
        for (AppRole r : AppRole.values()) {
            if (r.getLevel() < 10) {
                continue; // don't expose NEW_USER, nor SUPER_USER
            }
            Map<String, String> role = new HashMap<String, String>();
            role.put("value", String.valueOf(r.getLevel()));
            role.put("label", "_" + r.toString());
            roles.add(role);
        }
        context.put("roles", roles);

        final StringWriter writer = new StringWriter();
        t.merge(context, writer);

        resp.setContentType("application/javascript;charset=UTF-8");

        final PrintWriter pw = resp.getWriter();
        pw.println(writer.toString());
        pw.close();
    }

    /**
     * Check for the current user locale configuration and set it
     *
     * @param props
     */
    private void addLocale(Map<String, String> props) {
        final com.google.appengine.api.users.User currentGoogleUser = UserServiceFactory
                .getUserService().getCurrentUser();
        if (currentGoogleUser != null && currentGoogleUser.getEmail() != null) {
            final User currentUser = new UserDao().findUserByEmail(currentGoogleUser.getEmail());
            final String locale = currentUser.getLanguage();
            if (locale != null) {
                props.put("locale", locale);
            } else {
                props.put("locale", "en");
            }
        }
    }
}
