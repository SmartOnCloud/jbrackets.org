package org.jbrackets.parser.tokens;

import java.io.PrintWriter;
import java.util.Map;

import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public abstract class Block {

    protected PrintWriter wr;
    protected Map<String, Object> ctx;

    public void render(PrintWriter wr, Map<String, Object> model) {
	this.wr = wr;
	this.ctx = model;
	main();
    }

    public Object eval(String expr) {
	return eval(expr, ctx);
    }

    public static Object eval(String expr, Object ctx) {
	StandardEvaluationContext context = new StandardEvaluationContext();
	SpelExpressionParser parser = new SpelExpressionParser();
	
	context.addPropertyAccessor(new ReflectivePropertyAccessor());
	context.addPropertyAccessor(new BeanFactoryAccessor());
	context.addPropertyAccessor(new MapAccessor());
	// TODO make this "silent fail over" configurable
	try {
	    Expression parseExpression = parser.parseExpression(expr);
	    return parseExpression.getValue(context, ctx);
	} catch (SpelEvaluationException e) {
	    return null;
	}
    }

    public PrintWriter getWr() {
	return wr;
    }

    public Map<String, Object> getCtx() {
	return ctx;
    }

    abstract protected void main();
}
