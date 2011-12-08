package org.jbrackets.parser.tokens;

import static java.lang.String.format;

import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.Token;
import org.springframework.expression.spel.SpelParseException;

public class ExpToken extends TextToken {

    private final Token tok;

    public ExpToken(String text, Token tok) {
	super(text.trim());
	this.tok = tok;
    }

    @Override
    public String getInvocation() {
	return surroundBySpelParseExceptionCatch(tok,
		format("wr.print(eval(\"%s\"));\n", getText().toString()));
    }

    public void test() throws ParseException {
	try {

	} catch (SpelParseException e) {
	    String message = e.getMessage();
	    ParseException parseException = new ParseException();
	    parseException.setStackTrace(e.getStackTrace());
	    throw parseException;
	}

    }
}