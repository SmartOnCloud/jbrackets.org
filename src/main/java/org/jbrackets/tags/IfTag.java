package org.jbrackets.tags;

import static java.lang.String.format;

import java.util.HashMap;

import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.tokens.Block;

public class IfTag extends Tag {

    private final Block container;

    public IfTag(Block container) {
	super(container.getWr(),
		new HashMap<String, Object>(container.getCtx()));
	this.container = container;
    }

    public void evaluate(String expr, Class<? extends Block> ifblock,
	    Class<? extends Block> elseBlock) throws ParseException {
	try {
	    Object eval = container.eval(expr, ctx);
	    if (eval.equals(Boolean.TRUE))
		ifblock.newInstance()
			.setEvalContext(container.getEvalContext())
			.render(wr, ctx);
	    else
		elseBlock.newInstance()
			.setEvalContext(container.getEvalContext())
			.render(wr, ctx);
	} catch (InstantiationException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	}
    }

    public static String generateInvocation(String expr, String ifClassName,
	    String elseClassName) {
	return format("new %s(this).evaluate(\"%s\", %s.class, %s.class); \n",
		IfTag.class.getName(), expr, ifClassName, elseClassName);

    }
}
