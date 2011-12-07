package org.jbrackets.parser.tokens;

import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfToken extends BaseToken {
    private static Logger log = LoggerFactory.getLogger(IfToken.class);

    private String expr;
    private String ifClassName = "IF_" + generateClassName();
    private String elseClassName = "ELSE_" + generateClassName();

    private BlockToken elseBlock = new BlockToken();

    public IfToken() {
    }

    public void setParam(String param) {
	if (log.isDebugEnabled())
	    log.debug("expr:[" + param + "]");
	expr = param;
    }

    @Override
    public String getInvocation() {
	return format(
		"new IfTag(this).evaluate(\"%s\", %s.class, %s.class); \n",
		expr, ifClassName, elseClassName);
    }

    public BaseToken getIf() {
	return this;
    }

    public BaseToken getElse() {
	return elseBlock;
    }

    @Override
    public String getImplementation() {
	return class_construct(ifClassName, "Block", true,
		getTokens())
		+ class_construct(elseClassName, "Block", true,
			elseBlock.getTokens());
    }
}