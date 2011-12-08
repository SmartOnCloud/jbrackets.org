package org.jbrackets.parser.tokens;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.jbrackets.parser.ParseException;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public abstract class Block {

    protected PrintWriter wr;
    protected Map<String, Object> ctx;

    public void render(PrintWriter wr, Map<String, Object> model)
	    throws org.jbrackets.parser.ParseException {
	this.wr = wr;
	this.ctx = new HashMap<String, Object>(model);
	main();
    }

    public Object eval(String expr) throws ParseException {
	return eval(expr, ctx);
    }

    public static Object eval(String expr, Object ctx) throws ParseException {
	StandardEvaluationContext context = new StandardEvaluationContext();
	SpelExpressionParser parser = new SpelExpressionParser();
	context.addPropertyAccessor(new ReflectivePropertyAccessor());
	context.addPropertyAccessor(new BeanFactoryAccessor());
	context.addPropertyAccessor(new MapFailoverAccessor());
	// TODO add BeanResolver to have access to ctx beans
	// TODO register functions, i.e. date formating
	// context.registerFunction(name, method)
	Expression parseExpression = parser.parseExpression(expr);
	return parseExpression.getValue(context, ctx);
    }

    public PrintWriter getWr() {
	return wr;
    }

    public Map<String, Object> getCtx() {
	return ctx;
    }

    abstract protected void main() throws org.jbrackets.parser.ParseException;
}
