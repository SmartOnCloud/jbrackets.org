package org.jbrackets.parser.tokens;


public class ForLoopToken extends BaseToken {

    private String it;
    private String col;
    private String className = "FoorLoop_" + getGenClassName();

    public ForLoopToken() {
    }

    public void setParam(String param) {
	String[] split = param.split(" in ");
	it = split[0];
	col = split[1];
    }

    public ForLoopToken(String it, String col) {
	this.it = it;
	this.col = col;
    }

    @Override
    public String getInvocation() {
	StringBuilder s = new StringBuilder();
	s.append("\t\t");
	s.append("new ForEachTag(this).iterate(\"");
	s.append(it);
	s.append("\", \"");
	s.append(col);
	s.append("\", ");
	s.append(className);
	s.append(".class);\n");

	// s.append("new ForEachTag(this).iterate(\"job\", \"jobs\", Page2_ForLoop.class);");
	return s.toString();
    }

    @Override
    public String getImplementation() {
	StringBuilder s = new StringBuilder();
	s.append("\tstatic public class ");
	s.append(className);
	s.append(" extends Block {\n");

	s.append("\tprotected void main() {\n");
	for (BaseToken tok : getTokens())
	    s.append(tok.getInvocation());
	s.append("\t};\n");
	for (BaseToken tok : getTokens()) {
	    s.append(tok.getImplementation());
	}
	s.append("\t}\n");
	return s.toString();
    }

}
