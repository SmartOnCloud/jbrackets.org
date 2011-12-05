package org.jbrackets.forms;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.validation.FieldError;

/**
 * @author michal.jemala
 */
public class InputFileRenderer implements Renderer {

    public String renderField(Form form, Annotation annotation, Field field) {
	FileField metadata = (FileField) annotation;

	return format("<input type='file' id='%s' name='%s' />", getId(field),
		getName(metadata, field));
    }

    public String renderLabel(Form form, Annotation annotation, Field field,
	    FieldError error) {
	FileField metadata = (FileField) annotation;
	if (error != null) {
	    return format(
		    "<label for='%s' class='error' title='%s'>%s</label>",
		    getId(field), error.getDefaultMessage(),
		    getLabel(metadata, field));
	}
	return format("<label for='%s'>%s</label>", getId(field),
		getLabel(metadata, field));
    }

    private String getLabel(FileField metadata, Field field) {
	String label = metadata.label();
	return label.length() > 0 ? label : field.getName();
    }

    private String getId(Field field) {
	return "id_" + field.getName();
    }

    private String getName(FileField metadata, Field field) {
	String name = metadata.name();
	return name.length() > 0 ? name : field.getName();
    }

}
