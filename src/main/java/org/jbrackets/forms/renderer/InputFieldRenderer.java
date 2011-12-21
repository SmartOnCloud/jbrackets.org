package org.jbrackets.forms.renderer;

import static java.lang.String.format;

import java.lang.reflect.Field;

import org.jbrackets.forms.FieldMetadata;
import org.jbrackets.forms.Form;
import org.jbrackets.forms.annotation.InputField;

public class InputFieldRenderer extends RendererSupport<InputField> {

    static final String FIELD_FORMAT = "<input type='%s' id='%s' name='%s' value='%s' placeholder='%s' %s/>";
    static final String LABEL_FORMAT = "<label for='%s'>%s</label>";

    @Override
    protected Class<InputField> getAnnotationType() {
	return InputField.class;
    }

    @Override
    public String renderField(Form form, FieldMetadata descriptor) {
	Field field = descriptor.getField();
	InputField metadata = getAnnotation(descriptor);

	String markup = format(FIELD_FORMAT, getType(metadata), getId(field),
		getName(metadata, field), getValue(form, field),
		getPlaceholder(metadata), getAdditionalAttributes(metadata));
	return markup;
    }

    @Override
    public String renderLabel(Form form, FieldMetadata descriptor) {
	Field field = descriptor.getField();
	InputField metadata = getAnnotation(descriptor);

	String markup = format(LABEL_FORMAT, getId(field),
		getLabel(metadata, field));
	return markup;
    }

    // Helpers ----------------------------------------------------------------

    protected String getType(InputField metadata) {
	return metadata.type().getValue();
    }

    protected String getName(InputField metadata, Field field) {
	String name = metadata.name();
	return name.length() > 0 ? name : field.getName();
    }

    private String getLabel(InputField metadata, Field field) {
	String label = metadata.label();
	return label.length() > 0 ? label : field.getName();
    }

    protected String getPlaceholder(InputField metadata) {
	return metadata.placeholder();
    }

    protected String getAdditionalAttributes(InputField metadata) {
	StringBuilder sb = new StringBuilder();

	if (metadata.required())
	    sb.append("required='required' ");

	if (metadata.multiple())
	    sb.append("multiple='multiple' ");

	return sb.toString();
    }

}
