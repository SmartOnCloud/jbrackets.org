package org.jbrackets.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;

class JBracketsFunctions {

    private JBracketsFunctions() {
    }

    public static String date(Date date, String pattern) {
	return new SimpleDateFormat(pattern).format(date);
    }

    public static String escape(String str) {
	return StringEscapeUtils.escapeHtml(str);
    }

}
