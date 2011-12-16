package com.gallatinsystems.common.util;

/**
 * utility class for dealing with CSV files (handles things like escaping/unescaping quotes, commas and newlines)
 * 
 *
 */
public class CSVUtil {

	private static final String QUOTE = "\"";
	private static final String ESCAPED_QUOTE = "\"\"";
	private static char[] CHARACTERS_THAT_MUST_BE_QUOTED = { ',', '"', '\n' };

	public static String Escape(String s) {
		if (s.contains(QUOTE))
			s = s.replace(QUOTE, ESCAPED_QUOTE);

		for (char item : CHARACTERS_THAT_MUST_BE_QUOTED) {
			if (s.indexOf(item) > -1){
				s = QUOTE + s + QUOTE;
				break;
			}
		}
		return s;
	}

	public static String Unescape(String s) {
		if (s.startsWith(QUOTE) && s.endsWith(QUOTE)) {
			s = s.substring(1, s.length() - 2);
			if (s.contains(ESCAPED_QUOTE))
				s = s.replace(ESCAPED_QUOTE, QUOTE);
		}
		return s;
	}
}
