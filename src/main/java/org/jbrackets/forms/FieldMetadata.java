package org.jbrackets.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.validation.FieldError;

/**
 * @author michal.jemala
 */
public class FieldMetadata {

    private final Field field;
    private final Annotation annotation;
    private final Class<? extends Renderer> renderer;
    private FieldError error;

    public FieldMetadata(Field field, Annotation annotation,
	    Class<? extends Renderer> renderer) {
	this.field = field;
	this.annotation = annotation;
	this.renderer = renderer;
    }

    public Field getField() {
	return field;
    }

    public Annotation getAnnotation() {
	return annotation;
    }

    public Class<? extends Renderer> getRenderer() {
	return renderer;
    }

    public boolean isValid() {
	return field != null && annotation != null && renderer != null;
    }

    public void setError(FieldError error) {
	this.error = error;
    }

    public FieldError getError() {
	return error;
    }

    public boolean hasError() {
	return error != null;
    }

}
