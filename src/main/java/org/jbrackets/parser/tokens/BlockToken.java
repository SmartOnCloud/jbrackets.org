package org.jbrackets.parser.tokens;

import java.util.List;


public class BlockToken extends BaseToken {
    private String name;
    
    public BlockToken() {
    }

    public BlockToken(String name) {
	this.name = name;
    }

    public BlockToken(String name, List<BaseToken> tokens) {
	this.name = name;
	setTokens(tokens);
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String getInvocation() {
	StringBuilder s = new StringBuilder();
	s.append("\t\t").append(name).append("();\n");
	return s.toString();
    }

    @Override
    public String getImplementation() {
	StringBuilder s = new StringBuilder();
	s.append("\tprotected void ").append(name).append("() { \n");
	for (BaseToken tok : getTokens())
	    s.append(tok.getInvocation());
	s.append("\t}\n");
	for (BaseToken tok : getTokens())
	    s.append(tok.getImplementation());
	return s.toString();
    }
}
