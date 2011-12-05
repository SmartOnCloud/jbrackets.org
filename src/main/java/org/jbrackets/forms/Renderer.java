package org.jbrackets.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.validation.FieldError;

/**
 * @author michal.jemala
 */
public interface Renderer {

    String renderField(Form form, Annotation metadata, Field field);

    String renderLabel(Form form, Annotation metadata, Field field,
	    FieldError fieldError);

}
