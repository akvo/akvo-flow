/*
 *  Copyright (C) 2013 Stichting Akvo (Akvo Foundation)
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONObject;

public class StringsServlet extends HttpServlet {

    private static final long serialVersionUID = -5814616069972956097L;
    private static final Logger log = Logger.getLogger(StringsServlet.class
            .getClass().getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        final InputStream is_strings = this.getClass().getResourceAsStream(
                "/locale/ui-strings.properties");

        final InputStream is_en = this.getClass().getResourceAsStream(
                "/locale/en.properties");
        final InputStream is_fr = this.getClass().getResourceAsStream(
                "/locale/fr.properties");
        final InputStream is_es = this.getClass().getResourceAsStream(
                "/locale/es.properties");
        final InputStream is_pt = this.getClass().getResourceAsStream(
                "/locale/pt.properties");

        final Properties strings = new Properties();
        final Properties en = new Properties();
        final Properties fr = new Properties();
        final Properties es = new Properties();
        final Properties pt = new Properties();

        strings.load(is_strings);
        en.load(is_en);
        es.load(is_es);
        fr.load(is_fr);
        pt.load(is_pt);

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
            t = engine.getTemplate("Strings.vm");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not get the template `CurrentUser`", e);
            return;
        }

        final VelocityContext context = new VelocityContext();

        context.put("en", new JSONObject(strings).toString());
        context.put("es", new JSONObject(translateKeys(strings, es)).toString());
        context.put("fr", new JSONObject(translateKeys(strings, fr)).toString());
        context.put("pt", new JSONObject(translateKeys(strings, pt)).toString());

        final StringWriter writer = new StringWriter();
        t.merge(context, writer);

        resp.setContentType("application/javascript;charset=UTF-8");

        final PrintWriter pw = resp.getWriter();
        pw.println(writer.toString());
        pw.close();

    }

    private Map<String, String> translateKeys(Properties strings, Properties tr) {
        final Map<String, String> result = new HashMap<String, String>();
        Iterator<Object> keys = strings.keySet().iterator();
        while (keys.hasNext()) {
            String k = (String) keys.next();
            String v = tr.getProperty(strings.getProperty(k));
            if (v == null) {
                // log.log(Level.WARNING, "Translation for term " + k
                // + " not found, using English term");
                result.put(k, strings.getProperty(k));
            } else {
                result.put(k, v);
            }
        }
        return result;
    }
}
