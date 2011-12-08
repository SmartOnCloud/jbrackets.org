package org.jbrackets.tags;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.tokens.Block;

public class ForEachTag extends Tag {
    private int counter = 1;
    private int counter0 = 0;
    private ForEachTag parentloop;
    private final Block container;

    public ForEachTag(Block container) {
	super(container.getWr(),
		new HashMap<String, Object>(container.getCtx()));
	this.container = container;
	parentloop = (ForEachTag) ctx.get("forloop");
	ctx.put("forloop", this);
    }

    public void iterate(String it, String iterationKey,
	    Class<? extends Block> tmpl) throws ParseException {
	try {
	    Object value = container.eval(iterationKey, ctx);
	    if (value.getClass().isArray())
		value = Arrays.asList((Object[]) value);
	    if (value instanceof Map) {
		value = ((Map<?, ?>) value).entrySet();
	    }
	    Iterable<?> object = (Iterable<?>) value;
	    Block newInstance = tmpl.newInstance();
	    newInstance.setEvalContext(container.getEvalContext());
	    for (Object obj : object) {
		ctx.put(it, obj);
		newInstance.render(wr, this.ctx);
		counter++;
		counter0++;
	    }
	} catch (InstantiationException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	}
    }

    public int getCounter() {
	return counter;
    }

    public void setCounter(int counter) {
	this.counter = counter;
    }

    public int getCounter0() {
	return counter0;
    }

    public void setCounter0(int counter0) {
	this.counter0 = counter0;
    }

    public ForEachTag getParentloop() {
	return parentloop;
    }

    public void setParentloop(ForEachTag parentloop) {
	this.parentloop = parentloop;
    }

    public static String generateInvocation(String it, String col,
	    String className) {
	return format("new %s(this).iterate(\"%s\", \"%s\", %s.class);\n",
		ForEachTag.class.getName(), it, col, className);
    }
}
