package org.jbrackets.web;

import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.jbrackets.parser.tokens.MapFailoverAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class DefaultRegistrar implements StandardEvaluationContextRegistrat {

    @Override
    public void register(StandardEvaluationContext c) {
	c.addPropertyAccessor(new ReflectivePropertyAccessor());
	c.addPropertyAccessor(new BeanFactoryAccessor());
	c.addPropertyAccessor(new MapFailoverAccessor());

	try {
	    if (c.lookupVariable("date") == null)
		c.registerFunction("date", JBracketsFunctions.class.getMethod(
			"date", new Class<?>[] { Date.class, String.class }));
	    c.registerFunction("escapeHtml", StringEscapeUtils.class.getMethod(
		    "escapeHtml", new Class<?>[] { String.class }));

	} catch (NoSuchMethodException e) {
	    e.printStackTrace();
	    // never happens
	}
    }
}
