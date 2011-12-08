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
import org.jbrackets.parser.tokens.MapFailoverAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.servlet.view.InternalResourceView;

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

public class TemplateView extends InternalResourceView {
    private TemplateEngine templateEngine = new TemplateEngine();

    private StandardEvaluationContext createEvalContext() {
	StandardEvaluationContext context = new StandardEvaluationContext();
	context.addPropertyAccessor(new ReflectivePropertyAccessor());
	context.addPropertyAccessor(new BeanFactoryAccessor());
	context.addPropertyAccessor(new MapFailoverAccessor());
	return context;
    }

    protected void renderMergedOutputModel(Map<String, Object> model,
	    HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	String templateFile = getServletContext().getRealPath(getUrl());
	try {
	    String process = templateEngine.process(templateFile, "UTF-8",
		    createEvalContext(), model);
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
	URL resource = getClass().getResource("/error/error_message.html");
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
			"UTF-8", createEvalContext(), model));
    }
}