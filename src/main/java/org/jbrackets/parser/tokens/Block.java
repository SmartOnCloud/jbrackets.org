package org.jbrackets.parser.tokens;

import java.io.PrintWriter;
import java.util.Map;

import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
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
	context.addPropertyAccessor(new ReflectivePropertyAccessor());
	context.addPropertyAccessor(new BeanFactoryAccessor());
	context.addPropertyAccessor(new MapAccessor());
	SpelExpressionParser parser = new SpelExpressionParser();

	Expression pe = parser.parseExpression(expr);
	return pe.getValue(context, ctx);
    }

    public PrintWriter getWr() {
	return wr;
    }

    public Map<String, Object> getCtx() {
	return ctx;
    }

    abstract protected void main();
}
