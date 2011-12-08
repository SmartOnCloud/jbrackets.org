package org.jbrackets.parser.tokens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockToken extends BaseToken {
    private static Logger log = LoggerFactory.getLogger(BlockToken.class);

    private String name;

    public BlockToken() {
    }

    public BlockToken(String name) {
	setName(name);
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	if (log.isDebugEnabled())
	    log.debug("block name:[" + name + "]");
	this.name = name;
    }

    @Override
    public String getInvocation() {
	return String.format("%s(new java.util.HashMap(ctx));\n", name);
    }

    @Override
    public String getImplementation() {
	return method_construct(name);
    }
}
