package org.jbrackets.forms.renderer;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.jbrackets.forms.FieldMetadata;
import org.jbrackets.forms.Form;
import org.jbrackets.forms.annotation.FileField;
import org.springframework.validation.FieldError;

/**
 * @deprecated use @{FileRenderer} instead
 */
@Deprecated
public class InputFileRenderer extends RendererSupport<FileField> {

    @Override
    protected Class<FileField> getAnnotationType() {
	return FileField.class;
    }

    @Override
    public String renderField(Form form, FieldMetadata metadata) {
	return renderField(form, getAnnotation(metadata), metadata.getField());
    }

    @Override
    public String renderLabel(Form form, FieldMetadata metadata) {
	return renderLabel(form, getAnnotation(metadata), metadata.getField(),
		metadata.getError());
    }

    @Deprecated
    public String renderField(Form form, Annotation annotation, Field field) {
	FileField metadata = (FileField) annotation;

	return format("<input type='file' id='%s' name='%s' />", getId(field),
		getName(metadata, field));
    }

    @Deprecated
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

    private String getName(FileField metadata, Field field) {
	String name = metadata.name();
	return name.length() > 0 ? name : field.getName();
    }

}
