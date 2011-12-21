package org.jbrackets.forms.renderer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;

import org.jbrackets.forms.FieldMetadata;
import org.jbrackets.forms.Form;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Base class for all renderers. Provide access to:
 * <ul>
 * <li>WebApplicationContext</li>
 * <li>FormatingConversionService</li>
 * </ul>
 * 
 * <p>
 * Allows reneders to use Spring managed beans.
 * 
 * @see Renderer
 */
public abstract class RendererSupport<A extends Annotation> implements Renderer {

    /**
     * Returns <code>FormattingConversionService</code> from the Spring context
     * or <code>null</code> if no context is available or such bean has not bean
     * registered.
     */
    protected FormattingConversionService getFormattingConvertionService() {
	WebApplicationContext webApplicationContext = getWebApplicationContext();
	if (webApplicationContext != null) {
	    FormattingConversionService formattingConversionService = webApplicationContext
		    .getBean(FormattingConversionService.class);

	    return formattingConversionService;
	}

	return null;
    }

    /**
     * Returns <code>WebApplicationContext</code> or <code>null</code> if no
     * context is available.
     */
    protected WebApplicationContext getWebApplicationContext() {
	RequestAttributes requestAttributes = RequestContextHolder
		.currentRequestAttributes();
	if (requestAttributes instanceof ServletRequestAttributes) {
	    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
	    HttpServletRequest request = servletRequestAttributes.getRequest();

	    return RequestContextUtils.getWebApplicationContext(request);
	}

	return null;
    }
    
    protected abstract Class<A> getAnnotationType();

    protected A getAnnotation(FieldMetadata metadata) {
	Annotation annotation = metadata.getAnnotation();
	return getAnnotationType().cast(annotation);
    }

    protected String getId(Field field) {
	return "id_" + field.getName();
    }

    protected String getValue(Form form, Field field) {
	String result = "";

	Object value = getFieldValue(form, field);
	if (value != null) {
	    FormattingConversionService formatter = getFormattingConvertionService();
	    if (formatter != null)
		result = formatter.convert(value, String.class);
	    else
		result = String.valueOf(value);
	}

	return result;
    }

    protected Object getFieldValue(Form form, Field field) {
	Object value;

	try {
	    ReflectionUtils.makeAccessible(field);
	    value = field.get(form);
	} catch (Exception e) {
	    value = null;
	}

	return value;
    }

}
