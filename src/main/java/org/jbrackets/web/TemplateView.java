package org.jbrackets.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbrackets.TemplateEngine;
import org.springframework.web.servlet.view.InternalResourceView;


// USAGE:
//<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
//  <property name="prefix" value="/WEB-INF/views/" />
//  <property name="suffix" value=".html" />
//  <property name="viewClass" value="org.jbrackets.web.TemplateView" />
//</bean>

public class TemplateView extends InternalResourceView {
    private TemplateEngine templateEngine = new TemplateEngine();

    protected void renderMergedOutputModel(Map<String, Object> model,
	    HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	String templateFile = getServletContext().getRealPath(getUrl());
	String process = templateEngine.process(templateFile, model);
	response.setContentType(getContentType());
	response.getWriter().print(process);
    }
}