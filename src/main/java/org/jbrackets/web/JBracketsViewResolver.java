package org.jbrackets.web;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.velocity.VelocityView;

public class JBracketsViewResolver extends AbstractTemplateViewResolver {

    public JBracketsViewResolver() {
	setViewClass(requiredViewClass());
	setContentType("text/html; charset=UTF-8");
    }

    /**
     * Requires {@link VelocityView}.
     */
    @Override
    protected Class<?> requiredViewClass() {
	return TemplateView.class;
    }

}
