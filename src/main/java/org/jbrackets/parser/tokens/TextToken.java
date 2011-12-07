package org.jbrackets.parser.tokens;

import static java.lang.String.format;

import org.apache.commons.lang.StringEscapeUtils;

public class TextToken extends BaseToken {
    private StringBuilder text = new StringBuilder();

    public TextToken(String text) {
	this.text.append(replaceChars(text));
    }

    public String getText() {
	return text.toString();
    }

    public void setText(String text) {
	this.text = new StringBuilder(replaceChars(text));
    }

    @Override
    public void appendText(String text) {
	this.text.append(replaceChars(text));
    }

    private String replaceChars(String text) {
	return StringEscapeUtils.escapeJava(text);
    }

    @Override
    public String getInvocation() {
	return format("wr.print(\"%s\");\n", getText().toString());
    }

    @Override
    public String getImplementation() {
	// no implementation block needed
	return "";
    }
}