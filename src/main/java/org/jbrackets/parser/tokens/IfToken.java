package org.jbrackets.parser.tokens;

public class IfToken extends BaseToken {

    private String expr;
    private String ifClassName = "If_" + getGenClassName();
    private String elseClassName = "ElseIf_" + getGenClassName();

    private BlockToken elseBlock = new BlockToken();

    public IfToken() {
    }

    public void setParam(String param) {
	expr = param;
    }

    @Override
    public String getInvocation() {
	StringBuilder s = new StringBuilder();
	s.append("\t\tnew IfTag(this).evaluate(\"");
	s.append(expr);
	s.append("\", ");
	s.append(ifClassName);
	s.append(".class,");
	s.append(elseClassName);
	s.append(".class);\n");

	// s.append("new ForEachTag(this).iterate(\"job\", \"jobs\", Page2_ForLoop.class);");
	return s.toString();
    }

    public BaseToken getIf() {
	return this;
    }

    public BaseToken getElse() {
	return elseBlock;
    }

    @Override
    public String getImplementation() {
	StringBuilder s = new StringBuilder();
	s.append("\tstatic public class ");
	s.append(ifClassName);
	s.append(" extends Block {\n");

	s.append("\tprotected void main() {\n");
	for (BaseToken tok : getTokens())
	    s.append(tok.getInvocation());
	s.append("\t};\n");
	for (BaseToken tok : getTokens()) {
	    s.append(tok.getImplementation());
	}
	s.append("\t}\n");

	s.append("\tstatic public class ");
	s.append(elseClassName);
	s.append(" extends Block {\n");

	s.append("\tprotected void main() {\n");
	for (BaseToken tok : elseBlock.getTokens())
	    s.append(tok.getInvocation());
	s.append("\t};\n");
	for (BaseToken tok : elseBlock.getTokens()) {
	    s.append(tok.getImplementation());
	}
	s.append("\t}\n");
	return s.toString();
    }

}
