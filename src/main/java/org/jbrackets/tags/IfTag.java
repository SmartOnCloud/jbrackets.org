package org.jbrackets.tags;

import static java.lang.String.format;

import java.util.HashMap;

import org.jbrackets.parser.tokens.Block;

public class IfTag extends Tag {

    public IfTag(Block container) {
	super(container.getWr(),
		new HashMap<String, Object>(container.getCtx()));
    }

    public void evaluate(String expr, Class<? extends Block> ifblock,
	    Class<? extends Block> elseBlock) {
	try {
	    Object eval = Block.eval(expr, ctx);
	    if (eval.equals(Boolean.TRUE))
		ifblock.newInstance().render(wr, ctx);
	    else
		elseBlock.newInstance().render(wr, ctx);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static String generateInvocation(String expr, String ifClassName,
	    String elseClassName) {
	return format("new %s(this).evaluate(\"%s\", %s.class, %s.class); \n",
		IfTag.class.getName(), expr, ifClassName, elseClassName);

    }
}
