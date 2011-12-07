package org.jbrackets.parser.tokens;

import org.jbrackets.parser.ParseException;
import org.jbrackets.tags.ForEachTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForLoopToken extends BaseToken {

    private static Logger log = LoggerFactory.getLogger(ForLoopToken.class);

    private String it;
    private String col;
    private String className = "FORLOOP_" + generateClassName();

    public ForLoopToken() {
    }

    public void setParam(String param) throws ParseException {
	String[] split = param.split(" in ");
	if (split.length != 2)
	    throw new ParseException("expected: \"for it in iterable\" format.");
	it = split[0];
	col = split[1];
	if (log.isDebugEnabled())
	    log.debug("found two arguments:[" + it + "," + col + "]");
    }

    @Override
    public String getInvocation() {
	return ForEachTag.generateInvocation(it, col, className);
    }

    @Override
    public String getImplementation() {
	return class_construct(className, Block.class.getName(), true,
		getTokens());
    }

}
