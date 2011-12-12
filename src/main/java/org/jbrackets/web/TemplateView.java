package org.jbrackets.web;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbrackets.TemplateEngine;
import org.jbrackets.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.expression.EvaluationContext;
import org.springframework.web.servlet.view.AbstractTemplateView;

/**
 * USAGE:
 * 
 * <pre>
 * <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
 *   <property name="prefix" value="/WEB-INF/views/" />
 *   <property name="suffix" value=".html" />
 *   <property name="viewClass" value="org.jbrackets.web.TemplateView" />
 * </bean>
 * </pre>
 */

public class TemplateView extends AbstractTemplateView {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private TemplateEngine templateEngine;
    private EvaluationContext evalContext;

    /**
     * Invoked on startup. Looks for a single VelocityConfig bean to find the
     * relevant VelocityEngine for this factory.
     */
    @Override
    protected void initApplicationContext() throws BeansException {
	super.initApplicationContext();

	if (getTemplateEngine() == null) {
	    setTemplateEngine(autodetectTemplateEngine());
	}
    }

    public void setEvalContext(EvaluationContext evalContext) {
	this.evalContext = evalContext;
    }

    public TemplateEngine getTemplateEngine() {
	return templateEngine;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
	this.templateEngine = templateEngine;
    }

    /**
     * Autodetect a TemplateEngine via the ApplicationContext. Called if no
     * explicit TemplateEngine has been specified.
     * 
     * @return the TemplateEngine to use for VelocityViews
     * @see #getApplicationContext
     * @see #setTemplateEngine
     */
    protected TemplateEngine autodetectTemplateEngine() throws BeansException {
	try {
	    JBracketsConfig jBracketsConfig = BeanFactoryUtils
		    .beanOfTypeIncludingAncestors(getApplicationContext(),
			    JBracketsConfig.class, true, false);
	    log.info("using provided jBracketConfig.");
	    setEvalContext(jBracketsConfig.getEvaluationContext());
	    return jBracketsConfig.getTemplateEngine();
	} catch (NoSuchBeanDefinitionException ex) {
	    JBracketsConfig jBracketsConfig = new JBracketsConfig();
	    jBracketsConfig.afterPropertiesSet();
	    setEvalContext(jBracketsConfig.getEvaluationContext());
	    log.info("using default jBracketConfig.");
	    return jBracketsConfig.getTemplateEngine();
	}
    }

    protected void renderMergedTemplateModel(Map<String, Object> model,
	    HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	String templateFile = getServletContext().getRealPath(getUrl());
	try {
	    String process = templateEngine.process(templateFile, model);
	    response.setContentType(getContentType());
	    response.getWriter().print(process);
	} catch (ParseException e) {
	    generateErrorPage(model, request, response, e);
	}
    }

    private void generateErrorPage(Map<String, Object> model,
	    HttpServletRequest request, HttpServletResponse response,
	    ParseException e) throws IOException, ParseException,
	    URISyntaxException {
	URL resource = getClass().getResource(
		"/jbrackets_templates/error_message.html");
	StringWriter trace = new StringWriter();
	PrintWriter s = new PrintWriter(trace);
	e.printStackTrace(s);
	s.flush();

	model.put("ex_title", e.getClass().getName());
	model.put("ex_subtitle", "");
	model.put("ex_requestMethod", request.getMethod());
	model.put("ex_requestURI", request.getRequestURI());
	model.put("ex_stacktrace", escapeHtml(trace.toString()));
	model.put("ex_message", escapeHtml(e.getMessage()));

	response.setContentType(getContentType());
	response.getWriter().print(
		templateEngine.process(new File(resource.toURI()).getPath(),
			"UTF-8", evalContext, model));
    }

}