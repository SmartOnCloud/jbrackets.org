package org.jbrackets.tags;

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
}
