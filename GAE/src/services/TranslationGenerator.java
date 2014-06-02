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

	private static final Map<String, String> PATTERNS = new HashMap<String, String>();

	static {
		PATTERNS.put("{{t ", "}}");
		PATTERNS.put("{{tooltip ", "}}");
		PATTERNS.put("promptBinding=\"Ember.STRINGS.", "\"");
		PATTERNS.put("placeholderBinding=\"Ember.STRINGS.", "\"");
		PATTERNS.put("String.loc('", "'");
	}

	private static final String[] EXTS = { "handlebars", "js" };

	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.err.println("<Dashboard> and <output> directories are required");
			return;
		}

		final File sources = new File(args[0]);
		final File output = new File(args[1]);

		final Properties ui_strings = new Properties();
		ui_strings.load(new FileInputStream(new File(output,
				"/ui-strings.properties")));

		final Map<String, String> trlKeys = new HashMap<String, String>();
		final List<String> enValues = new ArrayList<String>();

		for (File f : (List<File>) FileUtils.listFiles(sources, EXTS, true)) {
			if (f.getAbsolutePath().contains("vendor")
					|| f.getAbsolutePath().contains("plugins")
					|| f.getAbsolutePath().contains("tests")) {
				continue; // skipping
			}

			final List<String> lines = FileUtils.readLines(f, "UTF-8");

			for (String line : lines) {
				if (containsTranslatableKeys(line)) {
					final List<String> keys = getKeys(line);
					if (!keys.isEmpty()) {
						for (String k : keys) {
							if (trlKeys.containsKey(k)) {
								continue; // skip
							}
							final String en = ui_strings.getProperty(k);
							if (en == null) {
								System.err.println("Translation key `"
												+ k
												+ "` not found in ui-strings.properties");
								ui_strings.put(k, "");
							}

							trlKeys.put(k, (en == null ? "" : en));

							if (en != null && !"".equals(en)
									&& !enValues.contains(en)) {
								enValues.add(en);
							}
						}
					}
				}
			}
		}

		Collections.sort(enValues);
		StringBuffer sb = new StringBuffer();
		for (String val : enValues) {
			sb.append(val.replaceAll(" ", "\\\\ ")).append(" = ").append(val)
					.append("\n");
		}
		FileUtils.writeStringToFile(new File(output, "/en.properties"),
				sb.toString(), "ISO-8859-1");

		final List<String> tmp = new ArrayList<String>(trlKeys.keySet());
		Collections.sort(tmp);
		final StringBuffer uisource = new StringBuffer();
		for (String ui : tmp) {
			uisource.append(ui).append(" = ").append(trlKeys.get(ui))
					.append("\n");
		}
		FileUtils.writeStringToFile(new File(output, "/ui-strings.properties"),
				uisource.toString(), "ISO-8859-1");
	}

	private static boolean containsTranslatableKeys(String line) {
		for (String pattern : PATTERNS.keySet()) {
			if (line.contains(pattern)) {
				return true;
			}
		}
		return false;
	}

	private static List<String> getKeys(String line) {

		if (!containsTranslatableKeys(line)) {
			return Collections.emptyList();
		}

		final List<String> keys = new ArrayList<String>();
		for (String pattern : PATTERNS.keySet()) {
			keys.addAll(getKeysFromLine(line, pattern, PATTERNS.get(pattern)));
		}

		return keys;
	}

	private static List<String> getKeysFromLine(String line, String prefix,
			String suffix) {

		final List<String> keys = new ArrayList<String>();

		int start = line.indexOf(prefix);
		int end = -1;

		if (start != -1) {
			start += prefix.length();
			end = line.indexOf(suffix, start);
		}

		while (start > 0 && end > 0) {
			keys.add(line.substring(start, end));
			start = line.indexOf(prefix, end + 1);
			if (start == -1) {
				break;
			}
			start = start + prefix.length();
			end = line.indexOf(suffix, start);
		}

		return keys;
	}

}
