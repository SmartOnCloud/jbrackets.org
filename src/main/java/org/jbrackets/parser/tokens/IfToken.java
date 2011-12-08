package org.jbrackets.parser.tokens;

import org.jbrackets.parser.Token;
import org.jbrackets.tags.IfTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfToken extends BaseToken {
    private static Logger log = LoggerFactory.getLogger(IfToken.class);

    private String expr;
    private String ifClassName = "IF_" + generateClassName();
    private String elseClassName = "ELSE_" + generateClassName();

    private BlockToken elseBlock = new BlockToken();

    private final Token tok;

    public IfToken(Token tok) {
	this.tok = tok;
    }

    public void setParam(String param) {
	if (log.isDebugEnabled())
	    log.debug("expr:[" + param + "]");
	expr = param;
    }

    @Override
    public String getInvocation() {
	return surroundBySpelParseExceptionCatch(tok,
		IfTag.generateInvocation(expr, ifClassName, elseClassName));
    }

    public BaseToken getIf() {
	return this;
    }

    public BaseToken getElse() {
	return elseBlock;
    }

    @Override
    public String getImplementation() {
	return class_construct(ifClassName, Block.class.getName(), true,
		getTokens())
		+ class_construct(elseClassName, Block.class.getName(), true,
			elseBlock.getTokens());
    }
}