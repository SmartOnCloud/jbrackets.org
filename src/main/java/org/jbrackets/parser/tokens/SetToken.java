package org.jbrackets.parser.tokens;

import org.apache.commons.lang.StringEscapeUtils;
import org.jbrackets.parser.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetToken extends BaseToken {
    private static Logger log = LoggerFactory.getLogger(SetToken.class);
    private String newVar;
    private String expr;
    private Token token;

    public SetToken(Token token) {
	this.token = token;
    }

    public void setParam(String param) {
	String[] split = param.split(" ");
	newVar = replaceChars(split[0]);
	expr = replaceChars(split[1]);

	if (log.isDebugEnabled())
	    log.debug("set '" + newVar + "': [" + expr + "]");
    }

    private String replaceChars(String text) {
	return StringEscapeUtils.escapeJava(text);
    }

    @Override
    public String getInvocation() {
	return surroundBySpelParseExceptionCatch(token, "ctx.put(\"" + newVar
		+ "\", eval(\"" + expr + "\"));\n");
    }

    @Override
    public String getImplementation() {
	return "";
    }
}