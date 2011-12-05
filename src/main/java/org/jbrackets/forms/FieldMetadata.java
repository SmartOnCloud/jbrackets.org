package org.jbrackets.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author michal.jemala
 */
public class FieldMetadata {

    private final Field field;
    private final Annotation annotation;
    private final Class<? extends Renderer> renderer;

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

}
