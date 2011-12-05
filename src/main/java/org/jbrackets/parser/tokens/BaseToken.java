package org.jbrackets.parser.tokens;

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

    protected String getGenClassName() {
	return new BigInteger(32, random).toString(32);

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
}