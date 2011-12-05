package org.jbrackets.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;

/**
 * @author michal.jemala
 */
public class Form {
    transient private Errors errors;

    public Form() {
    }

    protected String as_item(ItemWrapper w) {
	StringBuilder sb = new StringBuilder();

	for (FieldMetadata fd : getFieldsMetadata()) {
	    if (fd.isValid()) {
		Field field = fd.getField();
		Annotation annotation = fd.getAnnotation();
		Renderer renderer = instatiateRenderer(fd);

		sb.append(w.beforeItem(fd));

		sb.append(w.beforeItemLabel(fd));
		sb.append(renderer.renderLabel(this, annotation, field,
			errors != null ? errors.getFieldError(field.getName())
				: null));
		sb.append(w.afterItemLabel(fd));

		sb.append(w.beforeItemField(fd));
		sb.append(renderer.renderField(this, annotation, field));
		sb.append(w.afterItemField(fd));

		sb.append(w.afterItem(fd));
	    }
	}

	return sb.toString();
    }

    public String as_table() {
	return as_item(new DefaultItemWrapper() {
	    @Override
	    public String beforeItem(FieldMetadata metadata) {
		return "<tr>";
	    }

	    @Override
	    public String afterItem(FieldMetadata metadata) {
		return "</tr>";
	    }

	    @Override
	    public String beforeItemLabel(FieldMetadata metadata) {
		return "<td>";
	    }

	    @Override
	    public String afterItemLabel(FieldMetadata metadata) {
		return "</td>";
	    }

	    @Override
	    public String beforeItemField(FieldMetadata metadata) {
		return "<td>";
	    }

	    @Override
	    public String afterItemField(FieldMetadata metadata) {
		return "</td>";
	    }
	});
    }

    public String as_p() {
	return as_item(new DefaultItemWrapper() {
	    @Override
	    public String beforeItem(FieldMetadata metadata) {
		return "<p>";
	    }

	    @Override
	    public String afterItem(FieldMetadata metadata) {
		return "</p>";
	    }
	});
    }

    public String as_li() {
	return as_item(new DefaultItemWrapper() {
	    @Override
	    public String beforeItem(FieldMetadata metadata) {
		return "<li>";
	    }

	    @Override
	    public String afterItem(FieldMetadata metadata) {
		return "</li>";
	    }
	});
    }

    private Renderer instatiateRenderer(FieldMetadata fieldDefinition) {
	Renderer renderer = null;

	try {
	    Class<? extends Renderer> rendererClass = fieldDefinition
		    .getRenderer();
	    Constructor<? extends Renderer> constructor = rendererClass
		    .getDeclaredConstructor();
	    ReflectionUtils.makeAccessible(constructor);
	    renderer = constructor.newInstance();
	} catch (Exception e) {
	    // TODO add logging
	}

	return renderer;
    }

    protected List<FieldMetadata> getFieldsMetadata() {
	List<FieldMetadata> fieldDefinitions = new ArrayList<FieldMetadata>();

	for (Field field : getClass().getDeclaredFields()) {
	    Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
	    for (Annotation annotation : declaredAnnotations) {
		FormField markerAnnotation = annotation.annotationType()
			.getAnnotation(FormField.class);
		if (markerAnnotation != null) {
		    Class<? extends Renderer> renderer = markerAnnotation
			    .renderer();
		    FieldMetadata fieldDefinition = new FieldMetadata(field,
			    annotation, renderer);
		    fieldDefinitions.add(fieldDefinition);
		}
	    }
	}

	return fieldDefinitions;
    }

    public Form withErrors(Errors errors) {
	this.errors = errors;
	return this;
    }

    public Errors getErrors() {
	return errors;
    }
}
