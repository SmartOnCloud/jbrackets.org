package org.jbrackets.parser.tokens;

import static java.lang.String.format;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseToken {
    private List<BaseToken> tokens = new ArrayList<BaseToken>();
    private static SecureRandom random = new SecureRandom();

    protected List<BaseToken> getTokens() {
	return tokens;
    }

    protected String generateClassName() {
	return new BigInteger(32, random).toString(32).toUpperCase();

    }

    protected void setTokens(List<BaseToken> tokens) {
	this.tokens = tokens;
    }

    public void addToken(BaseToken tok) {
	tokens.add(tok);
    }

    public void appendText(String text) {
	TextToken baseToken = new TextToken(text);
	if (!tokens.isEmpty()) {
	    BaseToken lastToken = tokens.get(tokens.size() - 1);
	    if (TextToken.class.equals(lastToken.getClass())) {
		lastToken.appendText(text);
		return;
	    }
	}
	tokens.add(baseToken);
    }

    public abstract String getInvocation();

    public abstract String getImplementation();

    /**
     * Generates construct:
     * 
     * <pre>
     * public [static] class {className} extends {parentClass} {
     * 		protected main() {
     * 		   // inner tokens invocations
     *  	}
     *  	// inner tokens implementations
     * }
     * 
     * <pre>
     * 
     * @param s
     * @param className
     * @param tokens
     */
    protected String class_construct(String className, String parentClass,
	    boolean isStatic, List<BaseToken> toks) {
	StringBuilder s = new StringBuilder();
	s.append("// ---\n");
	s.append(format("public%s class %s extends %s {\n",
		isStatic ? " static" : "", className, parentClass));
	if (parentClass.equals("Block")) {
	    s.append("\tprotected void main() {\n");
	    for (BaseToken tok : toks)
		s.append("\t\t" + tok.getInvocation());
	    s.append("\t};\n");
	}
	for (BaseToken tok : toks)
	    s.append(tok.getImplementation());
	s.append("}\n");
	return s.toString();
    }

    protected String method_construct(String name) {
	StringBuilder s = new StringBuilder();
	s.append(format("\tprotected void %s() { \n", name));
	for (BaseToken tok : getTokens())
	    s.append("\t\t" + tok.getInvocation());
	s.append("\t}\n");
	for (BaseToken tok : getTokens())
	    s.append(tok.getImplementation());
	return s.toString();
    }
}