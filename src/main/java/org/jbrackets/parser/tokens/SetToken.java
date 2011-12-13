package org.jbrackets.parser.tokens;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetToken extends BaseToken {
    private static final String PARAM_PATTERN = "(?s)([^ \n\t]+)[ \n\t]+(.+)$";
    private static Logger log = LoggerFactory.getLogger(SetToken.class);

    private String newVar;
    private String expr;
    private Token token;

    public SetToken(Token token) {
	this.token = token;
    }

    public void setParam(String param) throws ParseException {
	Pattern pattern = Pattern.compile(PARAM_PATTERN);
	Matcher m = pattern.matcher(param);
	if (!m.matches())
	    throw new ParseException("missing expression in:" + param
		    + " line " + token.beginLine + ", column "
		    + token.beginColumn);

	newVar = replaceChars(m.group(1));
	expr = replaceChars(m.group(2));
	if (log.isDebugEnabled())
	    log.debug("set '" + newVar + "': [" + expr + "]");
    }

    private String replaceChars(String text) {
	return StringEscapeUtils.escapeJava(text);
    }

    @Override
    public String getInvocation() {
	return surroundBySpelParseExceptionCatch(token, "ctx.put(\"" + newVar
		+ "\", eval(\"" + expr + "\", ctx));\n");
    }

    @Override
    public String getImplementation() {
	return "";
    }
}