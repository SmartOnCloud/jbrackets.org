package org.jbrackets.parser.tokens;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.jbrackets.parser.ParseException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public abstract class Block {

    protected PrintWriter wr;
    protected Map<String, Object> ctx;

    protected EvaluationContext evalContext;

    public Block setEvalContext(EvaluationContext evalContext) {
	this.evalContext = evalContext;
	return this;
    }

    public EvaluationContext getEvalContext() {
	return evalContext;
    }

    public void render(PrintWriter wr, Map<String, Object> model)
	    throws org.jbrackets.parser.ParseException {
	this.wr = wr;
	this.ctx = new HashMap<String, Object>(model);
	main();
    }

    public Object eval(String expr, Object ctx) throws ParseException {
	SpelExpressionParser parser = new SpelExpressionParser();
	Expression parseExpression = parser.parseExpression(expr);
	return parseExpression.getValue(evalContext, ctx);
    }

    public PrintWriter getWr() {
	return wr;
    }

    public Map<String, Object> getCtx() {
	return ctx;
    }

    abstract protected void main() throws org.jbrackets.parser.ParseException;
}
