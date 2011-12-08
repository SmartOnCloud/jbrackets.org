package org.jbrackets.parser.tokens;

import static java.lang.String.format;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.Token;
import org.springframework.expression.spel.SpelParseException;

public abstract class BaseToken {
    private List<BaseToken> tokens = new ArrayList<BaseToken>();
    private static SecureRandom random = new SecureRandom();
    private BaseToken parent;

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
	tok.setParent(this);
    }

    private void setParent(BaseToken parent) {
	this.parent = parent;
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

    protected String getFilePath() {
	if (parent != null)
	    return parent.getFilePath();
	return "<unknown>";
    }

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
	s.append(format("public%s class %s extends %s {\n",
		isStatic ? " static" : "", className, parentClass));
	if (parentClass.equals(Block.class.getName())) {
	    s.append("\tprotected void main() throws org.jbrackets.parser.ParseException {\n");
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
	s.append(format(
		"\tprotected void %s() throws org.jbrackets.parser.ParseException { \n",
		name));
	for (BaseToken tok : getTokens())
	    s.append("\t\t" + tok.getInvocation());
	s.append("\t}\n");
	for (BaseToken tok : getTokens())
	    s.append(tok.getImplementation());
	return s.toString();
    }

    protected String surroundBySpelParseExceptionCatch(Token t,
	    String exprToSurround) {
	String msg = format("%s\\nline %d, column %d\\n", getFilePath(),
		t.beginLine, t.beginColumn);
	String peCls = ParseException.class.getName();
	String spelCls = SpelParseException.class.getName();

	StringBuilder s = new StringBuilder();
	s.append("try {\n\t\t\t");
	s.append(exprToSurround);
	s.append(format("\t\t} catch (%s e) {\n", spelCls));
	s.append(format(
		"\t\t\t%s parseException = new %s(\"%s\" + e.getMessage());\n",
		peCls, peCls, msg));
	s.append("\t\t\tparseException.setStackTrace(e.getStackTrace());\n");
	s.append("\t\t\tthrow parseException;\n");
	s.append("\t\t}\n");
	return s.toString();
    }
}