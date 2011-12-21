package org.jbrackets.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jbrackets.forms.annotation.FormField;
import org.jbrackets.forms.renderer.Renderer;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * @author michal.jemala
 */
public class Form {
    transient private Errors errors;

    public Form() {
    }

    protected String as_item(ItemWrapper w) {
	return as_item(getFieldsMetadata(), w);
    }

    protected String as_item(List<FieldMetadata> fieldMetadata, ItemWrapper w) {
	StringBuilder sb = new StringBuilder();

	for (FieldMetadata fm : fieldMetadata) {
	    if (fm.isValid()) {
		Field field = fm.getField();
		Annotation annotation = fm.getAnnotation();
		Renderer renderer = instatiateRenderer(fm);
		FieldError fieldError = errors != null ? errors
			.getFieldError(field.getName()) : null;
		fm.setError(fieldError);

		sb.append(w.beforeItem(fm));

		sb.append(w.beforeItemLabel(fm));
		sb.append(renderer.renderLabel(this, fm));
		sb.append(w.afterItemLabel(fm));

		sb.append(w.beforeItemField(fm));
		sb.append(renderer.renderField(this, fm));
		sb.append(w.afterItemField(fm));

		sb.append(w.afterItem(fm));
	    }
	}

	return sb.toString();
    }

    public String as_table() {
	List<FieldMetadata> fields = getFieldsMetadata();

	return as_item(fields, new DefaultItemWrapper() {
	    @Override
	    public String beforeItem(FieldMetadata metadata) {
		return metadata.hasError() ? "<tr class='error'>" : "<tr>";
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
		if (metadata.hasError()) {
		    return "<span>" + metadata.getError().getDefaultMessage()
			    + "</span></td>";
		} else {
		    return "</td>";
		}
	    }
	});
    }

    public String as_p() {
	List<FieldMetadata> fields = getFieldsMetadata();

	return as_item(fields, new DefaultItemWrapper() {
	    @Override
	    public String beforeItem(FieldMetadata metadata) {
		return metadata.hasError() ? "<p class='error'>" : "<p>";
	    }

	    @Override
	    public String afterItem(FieldMetadata metadata) {
		return "</p>";
	    }

	    @Override
	    public String afterItemField(FieldMetadata metadata) {
		if (metadata.hasError()) {
		    return "<span>" + metadata.getError().getDefaultMessage()
			    + "</span>";
		} else {
		    return "";
		}
	    }
	});
    }

    public String as_li() {
	List<FieldMetadata> fields = getFieldsMetadata();

	return as_item(fields, new DefaultItemWrapper() {
	    @Override
	    public String beforeItem(FieldMetadata metadata) {
		return metadata.hasError() ? "<li class='error'>" : "<li>";
	    }

	    @Override
	    public String afterItem(FieldMetadata metadata) {
		return "</li>";
	    }

	    @Override
	    public String afterItemField(FieldMetadata metadata) {
		if (metadata.hasError()) {
		    return "<span>" + metadata.getError().getDefaultMessage()
			    + "</span>";
		} else {
		    return "";
		}
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
	    e.printStackTrace();
	}

	return renderer;
    }

    protected List<FieldMetadata> getFieldsMetadata() {
	final List<FieldMetadata> fieldMetadata = new ArrayList<FieldMetadata>();

	ReflectionUtils.doWithFields(getClass(), new FieldCallback() {
	    @Override
	    public void doWith(Field field) throws IllegalArgumentException,
		    IllegalAccessException {
		Annotation[] declaredAnnotations = field
			.getDeclaredAnnotations();
		for (Annotation annotation : declaredAnnotations) {
		    FormField markerAnnotation = annotation.annotationType()
			    .getAnnotation(FormField.class);
		    if (markerAnnotation != null) {
			Class<? extends Renderer> renderer = markerAnnotation
				.renderer();
			FieldMetadata fieldDefinition = new FieldMetadata(
				field, annotation, renderer);
			fieldMetadata.add(fieldDefinition);
		    }
		}
	    }
	});

	return fieldMetadata;
    }

    public Form withErrors(Errors errors) {
	this.errors = errors;
	return this;
    }

    public Errors getErrors() {
	return errors;
    }
}
