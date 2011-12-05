package org.jbrackets.parser.tokens;

public class ExpToken extends TextToken {
    public ExpToken(String text) {
	super(text.trim());
    }

    @Override
    public String getInvocation() {
	StringBuilder s = new StringBuilder();
	s.append("\t\twr.print(eval(\"");
	s.append(getText().toString());
	s.append("\"));\n");
	return s.toString();
    }
}