package org.jbrackets.parser.tokens;

import static java.lang.String.format;

public class ExpToken extends TextToken {

    public ExpToken(String text) {
	super(text.trim());
    }

    @Override
    public String getInvocation() {
	return format("wr.print(eval(\"%s\"));  \n", getText().toString());
    }
}