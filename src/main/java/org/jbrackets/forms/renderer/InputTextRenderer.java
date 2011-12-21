package org.jbrackets.forms.renderer;

import static java.lang.String.format;

import java.lang.reflect.Field;

import org.jbrackets.forms.FieldMetadata;
import org.jbrackets.forms.Form;
import org.jbrackets.forms.Widget;
import org.jbrackets.forms.annotation.CharField;
import org.springframework.validation.FieldError;

@Deprecated
public class InputTextRenderer extends RendererSupport<CharField> {

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
    public String renderField(Form form, CharField metadata, Field field) {
	if (getWidget(metadata, field).equals(Widget.TEXTAREA))
	    return format("<textarea  id='%s' name='%s'  >%s</textarea>",
		    getId(field), getName(metadata, field),
		    getValue(form, field));

	return format("<input type='text' id='%s' name='%s' value='%s' />",
		getId(field), getName(metadata, field), getValue(form, field));
    }

    @Deprecated
    public String renderLabel(Form form, CharField metadata, Field field,
	    FieldError error) {
	if (error != null) {
	    return format(
		    "<label for='%s' class='error' title='%s'>%s</label>",
		    getId(field), error.getDefaultMessage(),
		    getLabel(metadata, field));
	}
	return format("<label for='%s'>%s</label>", getId(field),
		getLabel(metadata, field));
    }

    @Override
    protected Class<CharField> getAnnotationType() {
	return CharField.class;
    }

    private String getLabel(CharField metadata, Field field) {
	String label = metadata.label();
	return label.length() > 0 ? label : field.getName();
    }

    private Widget getWidget(CharField metadata, Field field) {
	return metadata.widget();
    }

    private String getName(CharField metadata, Field field) {
	String name = metadata.name();
	return name.length() > 0 ? name : field.getName();
    }

}
