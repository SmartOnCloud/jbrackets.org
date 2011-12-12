package org.jbrackets.web;

import org.jbrackets.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class JBracketsConfig implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private TemplateEngine engine;
    private StandardEvaluationContextRegistrat registrar;
    private StandardEvaluationContext evalContext;
    private String encoding = "UTF-8";

    public TemplateEngine getTemplateEngine() {
	return engine;
    }

    public EvaluationContext getEvaluationContext() {
	return evalContext;
    }

    public void setEvaluationContextRegistrar(
	    StandardEvaluationContextRegistrat registrar) {
	this.registrar = registrar;
    }

    public String getEncoding() {
	return encoding;
    }

    public void setEncoding(String encoding) {
	this.encoding = encoding;
    }

    @Override
    public void afterPropertiesSet() {
	engine = new TemplateEngine(encoding);
	evalContext = new StandardEvaluationContext();
	engine.setEvalContext(evalContext);
	if (registrar != null) {
	    if (log.isDebugEnabled())
		log.debug("applying custom registrar:" + registrar);
	    registrar.register(evalContext);
	}
	DefaultRegistrar defaultRegistrar = new DefaultRegistrar();
	if (log.isDebugEnabled())
	    log.debug("applying default registrar:" + defaultRegistrar);
	defaultRegistrar.register(evalContext);
    }
}