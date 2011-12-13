package org.jbrackets.parser.tokens;

import org.apache.commons.lang.StringEscapeUtils;
import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.Token;
import org.jbrackets.tags.ForEachTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForLoopToken extends BaseToken {

    private static Logger log = LoggerFactory.getLogger(ForLoopToken.class);

    private String it;
    private String col;
    private String className = "FORLOOP_" + generateClassName();

    private Token tok;

    public ForLoopToken(Token tok) {
	this.tok = tok;
    }

    public void setParam(String param) throws ParseException {
	String[] split = param.split(" in ");
	if (split.length != 2)
	    throw new ParseException("expected: \"for it in iterable\" format.");
	it = split[0];
	col = StringEscapeUtils.escapeJava(split[1]);
	if (log.isDebugEnabled())
	    log.debug("found two arguments:[" + it + "," + col + "]");
    }

    @Override
    public String getInvocation() {
	return surroundBySpelParseExceptionCatch(tok,
		ForEachTag.generateInvocation(it, col, className));
    }

    @Override
    public String getImplementation() {
	return class_construct(className, Block.class.getName(), true,
		getTokens());
    }

}
