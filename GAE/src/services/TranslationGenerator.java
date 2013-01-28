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

package services;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class TranslationGenerator {

	private static final String HPREFIX = "{{t ";
	private static final String HSUFFX = "}}";
	private static final String JSCALLPREFIX = "String.loc('";
	private static final String JSCALLSUFXX = "'";
	private static final String[] EXTS = { "handlebars", "js" };

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.err.println("<Dashboard> directory is required");
			return;
		}

		final File sources = new File(args[0]);

		final Properties ui_strings = new Properties();
		ui_strings.load(new FileInputStream(new File(sources,
				"/translations/locale/ui-strings.properties")));

		final Map<String, String> trlKeys = new HashMap<String, String>();
		final List<String> enValues = new ArrayList<String>();

		for (File f : (List<File>) FileUtils.listFiles(sources, EXTS, true)) {
			if (f.getAbsolutePath().contains("vendor")) {
				continue; // skipping
			}

			final List<String> lines = FileUtils.readLines(f, "UTF-8");

			for (String line : lines) {
				if (line.contains(HPREFIX) || line.contains(JSCALLPREFIX)) {
					final String key = getKey(line);
					if (key != null && !trlKeys.containsKey(key)) {
						final String en = ui_strings.getProperty(key);

						if (en == null) {
							System.err.println("Translation key `" + key
									+ "` not found in ui-strings.properties");
							ui_strings.put(key, "");
						}

						trlKeys.put(key, (en == null ? "" : en));

						if (en != null && !"".equals(en) && !enValues.contains(en)) {
							enValues.add(en);
						}
					}
				}
			}
		}

		Collections.sort(enValues);
		StringBuffer sb = new StringBuffer();
		for (String val : enValues) {
			sb.append(val).append(" = ").append(val).append("\n");
		}
		FileUtils.writeStringToFile(new File(sources,
				"/translations/locale/en.properties"), sb.toString(), "UTF-8");

		final List<String> tmp = new ArrayList<String>(trlKeys.keySet());
		Collections.sort(tmp);
		final StringBuffer uisource = new StringBuffer();
		for (String ui : tmp) {
			uisource.append(ui).append(" = ").append(trlKeys.get(ui))
					.append("\n");
		}
		FileUtils.writeStringToFile(new File(sources,
				"/translations/locale/ui-strings.properties"), uisource
				.toString(), "UTF-8");
	}

	private static String getKey(String line) {
		if (line.contains(HPREFIX)) {
			return getKeyFromTemplate(line);
		} else if (line.contains(JSCALLPREFIX)) {
			return getKeyFromJSCall(line);
		}
		return null;
	}

	private static String getKeyFromTemplate(String line) {
		return extractKeyFromLine(line, HPREFIX, HSUFFX);
	}

	private static String getKeyFromJSCall(String line) {
		return extractKeyFromLine(line, JSCALLPREFIX, JSCALLSUFXX);
	}

	private static String extractKeyFromLine(String line, String prefix,
			String suffix) {

		int start = line.indexOf(prefix) + prefix.length();
		int end = line.indexOf(suffix, start);

		if (start < 0 || end < 0) {
			return null;
		}
		return line.substring(start, end);
	}

}
